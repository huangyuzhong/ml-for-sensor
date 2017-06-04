package com.device.inspect.config.schedule;

import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.record.OfflineHourUnit;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.service.OfflineHourQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

//import statjava.lang.Math.toIntExact;

/**
 * Created by zyclincoln on 3/19/17.
 */
@Component
public class HourlyUtilityCalculation{
    private static final Logger LOGGER = LogManager.getLogger(HourlyUtilityCalculation.class);
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceInspectRepository deviceInspectRepository;

    @Autowired
    private DeviceInspectRunningStatusRepository deviceInspectRunningStatusRepository;

    @Autowired
    private DeviceHourlyUtilizationRepository deviceHourlyUtilizationRepository;

    @Autowired
    private  InspectDataRepository inspectDataRepository;

    @Autowired
    private OfflineHourQueue offlineHourQueue;

    private final static Integer offlineTimeStep = 300 * 1000;
    private final static Integer scanScope = 3600 * 1000;
    private final static Integer timeStep = 20 * 1000;
    private final static Integer powerInspectTypeId = 14;
    private final static Integer powerInspectSampleTime = 60*1000;

    private final static Integer lastStatusFlag = 10;
    private final static Integer total_retry_times = 10;
    private final static Integer maxTraceBackHours = 10;

    @Scheduled(cron = "0 10 * * * ? ")
    public void scheduleTask() {
        LOGGER.info("Start scanning utilization data");
        Date startScanTime = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        for(int i = 0; i < maxTraceBackHours; i++){
            long startTime = cal.getTimeInMillis() - i * scanScope;
            long endTime = startTime + scanScope;
            Date currentHour = new Date(startTime);
            Date targetHour = new Date(endTime);
            LOGGER.info("Begin scan missing utilization data from: " + currentHour + ", to " + targetHour);
            scanMissedHourForAllDevice(currentHour, targetHour);
        }

        while(!offlineHourQueue.recalculateRequest.isEmpty()){
            scanDeviceUtil(offlineHourQueue.recalculateRequest.get(0).getBeginTime(),
                    offlineHourQueue.recalculateRequest.get(0).getEndTime(),
                    offlineHourQueue.recalculateRequest.get(0).getDevice());
            LOGGER.info(String.format("Hourly Utilization: recalculate device %d, from %s, to %s.",
                    offlineHourQueue.recalculateRequest.get(0).getDevice().getId(),
                    offlineHourQueue.recalculateRequest.get(0).getBeginTime().toString(),
                    offlineHourQueue.recalculateRequest.get(0).getEndTime().toString()));
            offlineHourQueue.recalculateRequest.remove(0);
        }

        Date endScanTime = new Date();

        long timeCost = endScanTime.getTime() - startScanTime.getTime();

        LOGGER.info(String.format("--- This round of utilization scan takes %d ms ---", timeCost));
    }

    public void scanDeviceUtil(Date currentHour, Date targetHour, Device device){
        LOGGER.info(String.format("Hourly Utilization: scan device %s, at hour %s", device.getId(), currentHour));
        Integer lastStatus = lastStatusFlag;
        List<DeviceInspect> deviceInspects = deviceInspectRepository.findByDeviceId(device.getId());
        List<DeviceInspect> runningInspects = new ArrayList<>();
        List<List<InspectData>> listOfInspectData = new ArrayList<>();
        for (DeviceInspect deviceInspect : deviceInspects) {
            if (deviceInspect.getInspectPurpose() == 1) {
                LOGGER.info("Hourly Utilization: found status monitor " + deviceInspect.getId());
                runningInspects.add(deviceInspect);
                listOfInspectData.add(inspectDataRepository.
                        findByDeviceInspectIdAndCreateDateBetweenOrderByCreateDateAsc(deviceInspect.getId(),
                                currentHour, targetHour));
            }
        }

        // no inspect is used for record running status
        if (runningInspects.isEmpty() || listOfInspectData.isEmpty()) {
            LOGGER.info("Hourly Utilization: target device have not status inspect, pass");
	        return;
        }

        boolean noData = true;
        for(List<InspectData> inspectDatas : listOfInspectData){
            if(!inspectDatas.isEmpty()){
                noData = false;
		        LOGGER.info("Hourly Utilization: target device have data in target time interval");
		        break;
            }
        }
        if(noData){
            DeviceHourlyUtilization hourlyUtilization = deviceHourlyUtilizationRepository.findByDeviceIdIdAndStartHour(device.getId(), currentHour);
            if(hourlyUtilization == null){
                hourlyUtilization = new DeviceHourlyUtilization();
            }
            hourlyUtilization.setPowerUpperBound((float)0);
            hourlyUtilization.setPowerLowerBound((float)0);
            hourlyUtilization.setRunningTime(0);
            hourlyUtilization.setIdleTime(0);
            hourlyUtilization.setConsumedEnergy((float)0);
            hourlyUtilization.setDeviceId(device);
            hourlyUtilization.setStartHour(currentHour);
            deviceHourlyUtilizationRepository.save(hourlyUtilization);
	        LOGGER.info("Hourly Utilization: target device have no data in target time inverval, save zero record to database");
            return;
        }
        // initialize running status array
        List<Integer> runningStatusArray = new ArrayList<>(scanScope / timeStep);
        for (int i = 0; i < scanScope / timeStep; i++) {
            runningStatusArray.add(-1);
        }
        // scan every inspect data array
        for (List<InspectData> inspectDatas : listOfInspectData) {
            if (inspectDatas.isEmpty()) {
                continue;
            }

            LOGGER.info("Start scanning running status data and get hourly utilization. Inspect id: "
                    + inspectDatas.get(0).getDeviceInspect().getId());
            // get running status setup of current inspect
            List<DeviceInspectRunningStatus> runningStatuses = deviceInspectRunningStatusRepository.
                    findByDeviceInspectId(inspectDatas.get(0).getDeviceInspect().getId());
            for (InspectData inspectData : inspectDatas) {
                // use create time to calculate the index of inspect data in running status array
                Integer index = Long.valueOf(inspectData.getCreateDate().getTime() - currentHour.getTime()).intValue() / timeStep;
                Float value = Float.valueOf(inspectData.getResult());
                Integer status = -1;
                // calculate the running status of current inspect data
                for (DeviceInspectRunningStatus runningStatus : runningStatuses) {
                    if (value > runningStatus.getThreshold()) {
                        status = runningStatus.getDeviceRunningStatus().getLevel() > status ?
                                runningStatus.getDeviceRunningStatus().getLevel() : status;
                    }
                }
                // if running status of current inspect data is larger than original one, update it
                if (index < runningStatusArray.size() && runningStatusArray.get(index) < status) {
                    runningStatusArray.set(index, status);
                }
            }
        }

        Integer currentMiss = 0;
        LOGGER.info("Got running status data in this hour, size is " + runningStatusArray.size());
        for (int i = 0; i < runningStatusArray.size(); i++) {
            if (runningStatusArray.get(i) == -1) {
                if (currentMiss < offlineTimeStep) {
                    runningStatusArray.set(i, lastStatus);
                    currentMiss += timeStep;
                }
            } else {
                lastStatus = runningStatusArray.get(i);
                currentMiss = 0;
            }
        }

        Integer idleSecond = 0;
        Integer runningSecond = 0;
        for (int i = 0; i < runningStatusArray.size(); i++) {
            if (runningStatusArray.get(i) == 20) {
                runningSecond += timeStep / 1000;
            } else if (runningStatusArray.get(i) == 10) {
                idleSecond += timeStep / 1000;
            }
        }

        DeviceInspect powerInspect = deviceInspectRepository.findByInspectTypeIdAndDeviceId(powerInspectTypeId, device.getId());
        Float powerLower = new Float(0);
        Float powerUpper = new Float(0);
        Float energy = new Float(0);

        if (powerInspect != null) {
            LOGGER.info("Hourly Utilization: found power inspect " + powerInspect.getId());
            List<InspectData> powerInspectData = inspectDataRepository.
                    findByDeviceInspectIdAndCreateDateBetweenOrderByResultDesc(powerInspect.getId(),
                            currentHour, targetHour);


            LOGGER.info(String.format("Found %d power inspect data", powerInspectData.size()));
            if (powerInspectData != null && !powerInspectData.isEmpty()) {
                powerUpper = Float.parseFloat(powerInspectData.get(0).getResult());
                powerLower = Float.parseFloat(powerInspectData.get(powerInspectData.size() - 1).getResult());
                if (powerLower > powerUpper) {
                    Float temp = powerUpper;
                    powerUpper = powerLower;
                    powerLower = temp;
                }
            }

            for (InspectData powerData : powerInspectData) {
                energy += Float.parseFloat(powerData.getResult()) * powerInspectSampleTime;
            }
            energy = energy / 1000 / 1000;
        }
        DeviceHourlyUtilization hourlyUtilization = deviceHourlyUtilizationRepository.findByDeviceIdIdAndStartHour(device.getId(), currentHour);
        if (hourlyUtilization == null) {
            hourlyUtilization = new DeviceHourlyUtilization();
            hourlyUtilization.setDeviceId(device);
            hourlyUtilization.setStartHour(currentHour);
        }

        hourlyUtilization.setIdleTime(idleSecond);
        hourlyUtilization.setRunningTime(runningSecond);

        hourlyUtilization.setPowerUpperBound(powerUpper);
        hourlyUtilization.setPowerLowerBound(powerLower);

        hourlyUtilization.setConsumedEnergy(energy);
        deviceHourlyUtilizationRepository.save(hourlyUtilization);
    }

    public void scanMissedHourForAllDevice(Date currentHour, Date targetHour) {

        Iterable<Device> deviceList = deviceRepository.findAll();
        for (Device device : deviceList) {
            // if current hour of target device has no record, calculate it, and retry 10 times in total if db fail.
            DeviceHourlyUtilization deviceHourlyUtilization = deviceHourlyUtilizationRepository.findByDeviceIdIdAndStartHour(device.getId(), currentHour);
            if (deviceHourlyUtilization == null) {
                List<DeviceInspect> deviceInspects = deviceInspectRepository.findByDeviceId(device.getId());
                for (DeviceInspect deviceInspect : deviceInspects) {
                    if (deviceInspect.getInspectPurpose() == 1) {
                        // we use offline request queue to save missing request, just to clarify here
                        // that the queue contains request for both offline data recalculation and missing data recalculation
                        offlineHourQueue.recalculateRequest.add(new OfflineHourUnit(currentHour, targetHour, device));
                        LOGGER.info(String.format("Hourly Utility Scan: found device %d miss utilization data during %s and %s, request for recalculate. Total request %d",
                                device.getId(), currentHour.toString(), targetHour.toString(), offlineHourQueue.recalculateRequest.size()));
                        break;
                    }
                }
            }
        }
    }
}
