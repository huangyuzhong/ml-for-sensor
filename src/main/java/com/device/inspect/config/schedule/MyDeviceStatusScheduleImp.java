package com.device.inspect.config.schedule;

import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.firm.Building;
import com.device.inspect.common.model.firm.Company;
import com.device.inspect.common.model.firm.Room;
import com.device.inspect.common.model.firm.Storey;
import com.device.inspect.common.repository.device.*;
import com.device.inspect.common.repository.firm.BuildingRepository;
import com.device.inspect.common.repository.firm.CompanyRepository;
import com.device.inspect.common.repository.firm.RoomRepository;
import com.device.inspect.common.repository.firm.StoreyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/10/18.
 */
@Component
public class MyDeviceStatusScheduleImp implements  MySchedule {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private InspectDataRepository inspectDataRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StoreyRepository storeyRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AlertCountRepository alertCountRepository;

    @Autowired
    private DeviceOfflineRepository deviceOfflineRepository;

    @Autowired
    private MonitorDeviceRepository monitorDeviceRepository;

    @Scheduled(cron = "0 0/5 * * * ? ")
    @Override
    public void scheduleTask() {
        Iterable<Company> companies = companyRepository.findAll();
        if (null!=companies)
            for (Company company:companies){
                if (company.getEnable()==0)
                    continue;
                Integer companyLowAlert = 0;
                Integer companyHighAlert = 0;
                Integer companyOnline = 0;
                Integer companyOffline = 0;
                if (null!=company.getBuildings())
                    for (Building building : company.getBuildings()){
                        if (building.getEnable()==0)
                            continue;
                        Integer buildLowAlert = 0;
                        Integer buildHighAlert = 0;
                        Integer buildOnline = 0;
                        Integer buildOffline = 0;
                        if (null!=building.getFloorList())
                            for (Storey floor : building.getFloorList()){
                                if (floor.getEnable()==0)
                                    continue;
                                Integer floorLowAlert = 0;
                                Integer floorHighAlert = 0;
                                Integer floorOnline = 0;
                                Integer floorOffline = 0;
                                if (null!=floor.getRoomList())
                                    for (Room room:floor.getRoomList()){
                                        if (room.getEnable()==0)
                                            continue;
                                        Integer roomLowAlert = 0;
                                        Integer roomHighALert = 0;
                                        Integer roomOnline = 0;
                                        Integer roomOffline = 0;
                                        if (null!=room.getDeviceList())
                                            for (Device device :room.getDeviceList()){
                                                if (device.getEnable()==0)
                                                    continue;
                                                if (null!=device.getDeviceInspectList()) {
                                                    boolean alertjudge = false;
                                                    for (DeviceInspect deviceInspect : device.getDeviceInspectList()) {
                                                        InspectData inspectJudge = inspectDataRepository.
                                                                findTopByDeviceIdAndDeviceInspectIdOrderByCreateDateDesc(device.getId(), deviceInspect.getId());
                                                        if (inspectJudge.getType() != null) {
                                                            if (inspectJudge.getType().equals("high")) {
                                                                roomHighALert += 1;
                                                                alertjudge = false;
                                                                break;
                                                            } else if (inspectJudge.getType().equals("low")) {
                                                                alertjudge = true;
                                                            }
                                                        }
                                                    }
                                                    if (alertjudge)
                                                        roomLowAlert+=1;
                                                }
                                                InspectData inspectData = inspectDataRepository.findTopByDeviceIdOrderByCreateDateDesc(device.getId());
                                                long minutes = (new Date().getTime()-inspectData.getCreateDate().getTime())/(1000*60);
                                                if (minutes>5){
                                                    DeviceOffline deviceOffline = new DeviceOffline();
                                                    deviceOffline.setDevice(device);
                                                    deviceOffline.setOfflineDate(new Date());
                                                    deviceOfflineRepository.save(deviceOffline);
                                                    device.getMonitorDevice().setOnline(0);
                                                    roomOffline+=1;
                                                }else {
                                                    device.getMonitorDevice().setOnline(1);
                                                    roomOnline+=1;
                                                }
                                                Float offSocre = (float)50.0;
                                                Float alertScore = (float) 50.0;
                                                Calendar calendar = Calendar.getInstance();
                                                calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-1);
                                                Date dayOff = calendar.getTime();
                                                calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-7);
                                                Date weekOff = calendar.getTime();
                                                calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-30);
                                                Date monthOff = calendar.getTime();
                                                Long oneOff = deviceOfflineRepository.countByDeviceIdAndOfflineDateBetween(device.getId(),dayOff,new Date());
                                                Long sevenOff = deviceOfflineRepository.countByDeviceIdAndOfflineDateBetween(device.getId(),weekOff,dayOff);
                                                Long thirtyOff = deviceOfflineRepository.countByDeviceIdAndOfflineDateBetween(device.getId(),monthOff,weekOff);
                                                offSocre =(float) (offSocre-oneOff-sevenOff*0.5-thirtyOff*0.1);
                                                offSocre = offSocre>0?offSocre:0;

                                                Long oneHighAlert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                        2,dayOff,new Date());
                                                Long oneLowALert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                        1,dayOff,new Date());
                                                Long sevenHighALert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                        2,weekOff,dayOff);
                                                Long sevenLowALert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                        1,weekOff,dayOff);
                                                Long thirtyHighALert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                        2,monthOff,weekOff);
                                                Long thirtyLowALert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                        1,monthOff,weekOff);
                                                alertScore = (float)(alertScore-oneHighAlert-sevenHighALert*0.5-thirtyHighALert*0.25-
                                                        oneLowALert*0.5-sevenLowALert*0.25-thirtyLowALert*0.1);
                                                alertScore = alertScore>0?alertScore:0;
                                                device.setScore(String.valueOf(offSocre+alertScore));
                                                deviceRepository.save(device);
                                            }
                                        room.setHighAlert(roomHighALert);
                                        room.setLowAlert(roomLowAlert);
                                        room.setOnline(roomOnline);
                                        room.setOffline(roomOffline);
                                        room.setTotal(roomOffline+roomOnline);
                                        roomRepository.save(room);
                                        floorHighAlert += roomHighALert;
                                        floorLowAlert+=roomLowAlert;
                                        floorOnline+=roomOnline;
                                        floorOffline+=roomOffline;
                                    }
                                floor.setTotal(floorOffline+floorOnline);
                                floor.setOnline(floorOnline);
                                floor.setOffline(floorOffline);
                                floor.setLowAlert(floorLowAlert);
                                floor.setHighAlert(floorHighAlert);
                                storeyRepository.save(floor);
                                buildHighAlert+=floorHighAlert;
                                buildLowAlert+=floorLowAlert;
                                buildOnline+=floorOnline;
                                buildOffline+=floorOffline;
                            }
                        building.setTotal(buildOnline+buildOffline);
                        building.setOnline(buildOnline);
                        building.setOffline(buildOffline);
                        building.setHighAlert(buildHighAlert);
                        building.setLowAlert(buildLowAlert);
                        buildingRepository.save(building);
                        companyOnline+=buildOnline;
                        companyOffline+=buildOffline;
                        companyHighAlert+=buildHighAlert;
                        companyLowAlert+=buildLowAlert;
                    }
                company.setTotal(companyOffline+companyOnline);
                company.setLowAlert(companyLowAlert);
                company.setHighAlert(companyHighAlert);
                company.setOnline(companyOnline);
                company.setOffline(companyOffline);
                companyRepository.save(company);
            }
    }
}
