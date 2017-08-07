package com.device.inspect.config.schedule;

import com.device.inspect.Application;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.record.OfflineHourUnit;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.service.OfflineHourQueue;
import com.device.inspect.common.util.transefer.InspectProcessTool;
import com.device.inspect.common.util.transefer.StringDate;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.impl.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

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


  //  @Autowired
  //  private  InspectDataRepository inspectDataRepository;

    @Autowired
    private OfflineHourQueue offlineHourQueue;

    private final static Integer offlineTimeStep = 300 * 1000;
    private final static Integer scanScope = 3600 * 1000;
    private final static Integer timeStep = 20 * 1000;
    private final static Integer[] powerInspectTypeId = {14, 25, 42};
    private final static Integer powerInspectSampleTime = 60*1000;

    private final static Integer lastStatusFlag = 10;
    private final static Integer total_retry_times = 10;
    private final static Integer maxTraceBackHours = 10;

    @Scheduled(cron = "0 10 * * * ? ")
    public void executeInternal(){
        LOGGER.info("Start scanning utilization data");

        Date startScanTime = new Date();

        /*
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
        */

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
        List<DeviceInspect> lastHourRunningInspects = deviceInspectRepository.findByDeviceIdAndInspectPurpose(device.getId(), 1);

        /*
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
        */

        Map<DeviceInspect, List<DeviceInspectRunningStatus>> inspectStatusMap = new HashMap<>();

        for(DeviceInspect deviceInspect: lastHourRunningInspects){
            List<DeviceInspectRunningStatus> runningStatuses = deviceInspectRunningStatusRepository.findByDeviceInspectId(deviceInspect.getId());
            if(runningStatuses == null || runningStatuses.size() == 0){
                LOGGER.info(String.format("Device %d, inpsect %d, has no running status setup", device.getId(), deviceInspect.getId()));
                continue;
            }
            else{
                runningInspects.add(deviceInspect);
                inspectStatusMap.put(deviceInspect, runningStatuses);
            }
        }

        if(runningInspects.size() == 0){
            LOGGER.info("There is no active status inspects for device " + device.getId());
            return;
        }

        List<List<List<Object>>> listOfInspectData = new ArrayList<>();
        Date lastHourTime = new Date();
        lastHourTime.setTime(currentHour.getTime() - 5*60*1000);

        for (DeviceInspect deviceInspect : runningInspects) {
            LOGGER.info("Hourly Utilization: found status monitor " + deviceInspect.getId());

            List<List<Object>> inspectDataList = Application.influxDBManager.readTelemetryInTimeRange(
                    deviceInspect.getInspectType().getMeasurement(),
                    device.getId(), deviceInspect.getId(), currentHour, targetHour, Calendar.SECOND);


            List<List<Object>> lastHourStatus = Application.influxDBManager.readTelemetryInTimeRange(
                    deviceInspect.getInspectType().getMeasurement(),
                    device.getId(), deviceInspect.getId(), lastHourTime, currentHour, Calendar.SECOND);

            // calculate running status of current device inspect in last hour
            if(lastHourStatus != null && lastHourStatus.size() > 0){
                List<DeviceInspectRunningStatus> runningStatuses = deviceInspectRunningStatusRepository.
                        findByDeviceInspectId(deviceInspect.getId());
                Integer lastStatusOfCurrentInspect = lastStatusFlag;
                for(DeviceInspectRunningStatus runningStatus : runningStatuses){
                    if(runningStatus.getThreshold() < Float.parseFloat(lastHourStatus.get(lastHourStatus.size() - 1).get(1).toString())){
                        lastStatusOfCurrentInspect = runningStatus.getDeviceRunningStatus().getLevel() > lastStatusOfCurrentInspect ?
                                runningStatus.getDeviceRunningStatus().getLevel() : lastStatusOfCurrentInspect;
                    }
                }
                LOGGER.info(String.format("Hourly Utilization: inspect %d has running level %d in last hour", deviceInspect.getId(), lastStatusOfCurrentInspect));
                lastStatus = lastStatusOfCurrentInspect > lastStatus ? lastStatusOfCurrentInspect : lastStatus;
                LOGGER.info("Hourly Utilization: update last status to " + lastStatus.toString());
            }

            if(inspectDataList != null){
                listOfInspectData.add(inspectDataList);

            }

        }

        // no inspect is used for record running status
        if ( listOfInspectData.isEmpty()) {
            LOGGER.info("Hourly Utilization: target device have not status inspect data, pass");
	        return;
        }

        boolean noData = true;

        /*
        for(List<InspectData> inspectDatas : listOfInspectData){
            if(!inspectDatas.isEmpty()){
                noData = false;
		        LOGGER.info("Hourly Utilization: target device have data in target time interval");
		        break;
            }
        }
        */
        for(List<List<Object>> inspectDataList : listOfInspectData){
            if(!inspectDataList.isEmpty()){
                noData = false;
                LOGGER.info("Hourly Utilization: target device have data in target time interval");
                break;
            }
        }
        if(noData){

            Application.influxDBManager.writeHourlyUtilization(currentHour, device.getId(), device.getName(), device.getDeviceType().getName(),
                    0,0,0,0,0,false);

	        LOGGER.info("Hourly Utilization: target device have no data in target time inverval, save zero record to database");
            return;
        }

        // initialize running status array
        List<Integer> runningStatusArray = new ArrayList<>(scanScope / timeStep);
        for (int i = 0; i < scanScope / timeStep; i++) {
            runningStatusArray.add(-1);
        }



        // scan every inspect data array
        for(int i=0; i<listOfInspectData.size(); i++){
            List<List<Object>> inspectDataList = listOfInspectData.get(i);

            if(inspectDataList.isEmpty()){
                continue;
            }
            LOGGER.info("Start scanning running status data and get hourly utilization. Inspect id: "
                    + runningInspects.get(i).getId());
            // get running status setup of current inspect
            List<DeviceInspectRunningStatus> runningStatuses = inspectStatusMap.get(runningInspects.get(i));

            try {
                for (List<Object> inspectData : inspectDataList) {
                    // use create time to calculate the index of inspect data in running status array
                    long tsTime = TimeUtil.fromInfluxDBTimeFormat(inspectData.get(0).toString());
                    Integer indexTimeSlot = Long.valueOf(tsTime - currentHour.getTime()).intValue() / timeStep;
                    Float value = Float.valueOf(inspectData.get(1).toString());
                    Integer status = -1;
                    // calculate the running status of current inspect data
                    for (DeviceInspectRunningStatus runningStatus : runningStatuses) {
                        if (value > runningStatus.getThreshold()) {
                            status = runningStatus.getDeviceRunningStatus().getLevel() > status ?
                                    runningStatus.getDeviceRunningStatus().getLevel() : status;
                        }
                    }
                    // if running status of current inspect data is larger than original one, update it
                    if (indexTimeSlot < runningStatusArray.size() && runningStatusArray.get(indexTimeSlot) < status) {
                        runningStatusArray.set(indexTimeSlot, status);
                    }
                }
            }catch(Exception e){
                LOGGER.error(String.format("Failed parsing inspect data to running status series. Err: %s", e.toString()));
                continue;
            }

        }

        /*

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
        */

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

        DeviceInspect powerInspect = null;

        for(Integer typeId: powerInspectTypeId) {
            DeviceInspect queryPowerInspect = deviceInspectRepository.findByInspectTypeIdAndDeviceId(typeId, device.getId());
            if(queryPowerInspect != null){
                powerInspect = queryPowerInspect;
                break;
            }
        }


        Float powerLower = new Float(0);
        Float powerUpper = new Float(0);
        Float energy = new Float(0);

        if (powerInspect != null) {
            LOGGER.info("Hourly Utilization: found power inspect " + powerInspect.getId());

            List<List<Object>> powerInspectDataList = Application.influxDBManager.readTelemetryInTimeRange(
                    powerInspect.getInspectType().getMeasurement(),
                    device.getId(), powerInspect.getId(), currentHour, targetHour, Calendar.SECOND);

            LOGGER.info(String.format("Found %d power inspect data", powerInspectDataList.size()));
            if (powerInspectDataList != null && !powerInspectDataList.isEmpty()) {

                Float powerValue = Float.parseFloat(powerInspectDataList.get(0).get(1).toString());
                powerLower = powerValue;
                powerUpper = powerValue;
                energy += powerValue * powerInspectSampleTime;

                for(int i=1; i<powerInspectDataList.size(); i++){
                    powerValue = Float.parseFloat(powerInspectDataList.get(i).get(1).toString());
                    if(powerValue < powerLower){
                        powerLower = powerValue;
                    }else if(powerValue > powerUpper){
                        powerUpper = powerValue;
                    }

                    energy += powerValue * powerInspectSampleTime;
                }
            }

            /*
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


            for (InspectData powerData : powerInspectDataL) {
                energy += Float.parseFloat(powerData.getResult()) * powerInspectSampleTime;
            }
            */

            energy = energy / 1000 / 1000;
        }

        if(!Application.influxDBManager.writeHourlyUtilization(currentHour, device.getId(), device.getName(), device.getDeviceType().getName(),
                runningSecond, idleSecond, powerLower, powerUpper, energy, true)){
            LOGGER.error("Failed to write new hourly utilization data to influxdb for device " + device.getId());
        }


    }

    public void scanMissedHourForAllDevice(Date currentHour, Date targetHour) {

        Iterable<Device> deviceList = deviceRepository.findAll();
        for (Device device : deviceList) {
            // if current hour of target device has no record, calculate it, and retry 10 times in total if db fail.

            List<List<Object>> deviceHourlyUtilizationData = Application.influxDBManager.readDeviceUtilizationInTimeRange(device.getId(), currentHour, DateUtils.addMinutes(currentHour, 10));

            if (deviceHourlyUtilizationData == null || deviceHourlyUtilizationData.size() == 0){
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
