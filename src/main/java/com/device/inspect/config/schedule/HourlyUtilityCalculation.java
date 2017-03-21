package com.device.inspect.config.schedule;

import com.device.inspect.common.model.device.*;
import com.device.inspect.common.repository.device.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

//import statjava.lang.Math.toIntExact;

/**
 * Created by zyclincoln on 3/19/17.
 */
@Component
public class HourlyUtilityCalculation implements MySchedule{

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

    private final static Integer offlineTimeStep = 300 * 1000;
    private final static Integer scanScope = 3600 * 1000;
    private final static Integer timeStep = 20 * 1000;
    private final static Integer powerInspectTypeId = 14;
    private final static Integer powerInspectSampleTime = 60*1000;
    @Scheduled(cron = "0 0 0/1 * * ? ")
    @Override
    public void scheduleTask() {
        Integer lastStatus = 10;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();
        long endTime = startTime + scanScope;
        Date currentHour = new Date(startTime);
        Date targetHour = new Date(endTime);
        System.out.println("Begin scan: " + currentHour + ", " + targetHour);

        Iterable<Device> deviceList = deviceRepository.findAll();
        for (Device device : deviceList) {

            List<DeviceInspect> deviceInspects = deviceInspectRepository.findByDeviceId(device.getId());
            List<DeviceInspect> runningInspects = new ArrayList<>();
            List<List<InspectData>> listOfInspectData = new ArrayList<>();
            for (DeviceInspect deviceInspect : deviceInspects) {
                if (deviceInspect.getInspectPurpose() == 1) {
                    runningInspects.add(deviceInspect);
                    listOfInspectData.add(inspectDataRepository.
                            findByDeviceInspectIdAndCreateDateBetweenOrderByCreateDateAsc(deviceInspect.getId(),
                                    currentHour, targetHour));
                }
            }

            // no inspect is used for record running status
            if (runningInspects.isEmpty() || listOfInspectData.isEmpty()) {
                continue;
            }

            List<Integer> runningStatusArray = new ArrayList<>(scanScope / timeStep);
            for (int i = 0; i < scanScope / timeStep; i++) {
                runningStatusArray.add(-1);
            }

            for (List<InspectData> inspectDatas : listOfInspectData) {
                if (inspectDatas.isEmpty()) {
                    continue;
                }
                List<DeviceInspectRunningStatus> runningStatuses = deviceInspectRunningStatusRepository.
                        findByDeviceInspectId(inspectDatas.get(0).getDeviceInspect().getId());
                for (InspectData inspectData : inspectDatas) {
                    Integer index = Long.valueOf(inspectData.getCreateDate().getTime() - startTime).intValue() / timeStep;
                    Float value = Float.valueOf(inspectData.getResult());
                    Integer status = -1;
                    for (DeviceInspectRunningStatus runningStatus : runningStatuses) {
                        if (value > runningStatus.getThreshold()) {
                            status = runningStatus.getDeviceRunningStatus().getLevel() > status ?
                                    runningStatus.getDeviceRunningStatus().getLevel() : status;
                        }
                    }
                    if (index < runningStatusArray.size() && runningStatusArray.get(index) < status) {
                        runningStatusArray.set(index, status);
                    }
                }
            }

            Integer currentMiss = 0;
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
            List<InspectData> powerInspectData = inspectDataRepository.
                    findByDeviceInspectIdAndCreateDateBetweenOrderByRealValueDesc(powerInspect.getId(),
                            currentHour, targetHour);

            Float powerLower = new Float(0);
            Float powerUpper = new Float(0);

            if (powerInspectData != null && !powerInspectData.isEmpty()) {
                powerUpper = Float.parseFloat(powerInspectData.get(0).getResult());
                powerLower = Float.parseFloat(powerInspectData.get(powerInspectData.size() - 1).getResult());
                if(powerLower > powerUpper){
                    Float temp = powerUpper;
                    powerUpper = powerLower;
                    powerLower = temp;
                }
            }

            Float energy = new Float(0);
            for(InspectData powerData : powerInspectData){
                energy += Float.parseFloat(powerData.getResult()) * powerInspectSampleTime;
            }
            energy /= 1000;

            DeviceHourlyUtilization hourlyUtilization = deviceHourlyUtilizationRepository.findByDeviceIdIdAndStartHour(device.getId(), currentHour);
            if(hourlyUtilization == null){
                hourlyUtilization = new DeviceHourlyUtilization();
                hourlyUtilization.setDeviceId(device);
                hourlyUtilization.setStartHour(currentHour);
            }
;
            hourlyUtilization.setIdleTime(idleSecond);
            hourlyUtilization.setRunningTime(runningSecond);

            hourlyUtilization.setPowerUpperBound(powerUpper);
            hourlyUtilization.setPowerLowerBound(powerLower);

            hourlyUtilization.setConsumedEnergy(energy/1000);
            deviceHourlyUtilizationRepository.save(hourlyUtilization);
        }

    }


}
