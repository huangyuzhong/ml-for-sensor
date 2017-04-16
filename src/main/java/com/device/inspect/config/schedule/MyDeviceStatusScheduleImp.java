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
import com.device.inspect.controller.MessageController;
import com.device.inspect.controller.SocketMessageApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.text.DateFormat;

/**
 * Created by Administrator on 2016/10/18.
 */
@Component
public class MyDeviceStatusScheduleImp implements  MySchedule {
    private static final Logger logger = LogManager.getLogger(MyDeviceStatusScheduleImp.class);

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

    @Autowired
    private DeviceInspectRepository deviceInspectRepository;

    @Autowired
    private MessageController messageController;

    static private Integer bettaryInspectId = 9;

    /**
     * 从当前时间开始定时每隔10分钟刷新一次
     * 刷新设备的高级报警数量，低级报警数量，在线数量，掉线数量
     * 刷新室的高级报警数量，低级报警数量，在线数量，掉线数量
     * 刷新层的高级报警数量，低级报警数量，在线数量，掉线数量
     * 刷新楼的高级报警数量，低级报警数量，在线数量，掉线数量
     * 刷新公司的高级报警数量，低级报警数量，在线数量，掉线谁昂
     */
    @Scheduled(cron = "0 0/3 * * * ? ")
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
                Float companyScore = (float)0;
                List<Building> buildingList = buildingRepository.findByCompanyIdAndEnable(company.getId(),1);
                if (null!=buildingList)
                    for (Building building : buildingList){
                        if (building.getEnable()==0)
                            continue;
                        Integer buildLowAlert = 0;
                        Integer buildHighAlert = 0;
                        Integer buildOnline = 0;
                        Integer buildOffline = 0;
                        Float buildScore = (float)0;
                        List<Storey> floorList = storeyRepository.findByBuildIdAndEnable(building.getId(),1);
                        if (null!=floorList)
                            for (Storey floor : floorList){
                                if (floor.getEnable()==0)
                                    continue;
                                Integer floorLowAlert = 0;
                                Integer floorHighAlert = 0;
                                Integer floorOnline = 0;
                                Integer floorOffline = 0;
                                Float floorScore = (float)0;
                                List<Room> roomList = roomRepository.findByFloorIdAndEnable(floor.getId(),1);
                                if (null!=roomList)
                                    for (Room room:roomList){
                                        if (room.getEnable()==0)
                                            continue;
                                        Integer roomLowAlert = 0;
                                        Integer roomHighALert = 0;
                                        Integer roomOnline = 0;
                                        Integer roomOffline = 0;
                                        Float roomScore = (float)0;
                                        List<Device> deviceList = deviceRepository.findByRoomIdAndEnable(room.getId(),1);
                                        if (null!=deviceList)
                                            for (Device device :deviceList){
                                                logger.info("Scan Device id: " + device.getId());
						if (device.getEnable()==0)
                                                    continue;
                                                List<DeviceInspect> deviceInspectList = deviceInspectRepository.findByDeviceId(device.getId());
                                                MonitorDevice monitorDevice = monitorDeviceRepository.findByDeviceId(device.getId());
                                                if (null!=deviceInspectList) {
                                                    boolean alertjudge = false;
                                                    for (DeviceInspect deviceInspect : deviceInspectList) {
                                                        InspectData inspectJudge = inspectDataRepository.
                                                                findTopByDeviceIdAndDeviceInspectIdOrderByCreateDateDesc(device.getId(), deviceInspect.getId());
                                                        if (null!=inspectJudge &&inspectJudge.getType() != null) {
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
                                                boolean currentDeviceOnLine = false;
                                                if (null!=inspectData&&null!=inspectData.getCreateDate()){
                                                    Date currentTime = new Date();
                                                    Date reportTime = inspectData.getCreateDate();

						                            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
						                            long minutes = (currentTime.getTime() - reportTime.getTime())/(1000*60);
                                                    logger.info("Scan Device Device Life -- Id: " + device.getId() + ", minutes since last report: " + minutes + ", report time: " + df.format(reportTime) + ", current time: " + df.format(currentTime));
						                            if (minutes>5){
						                                currentDeviceOnLine = false;
                                                        DeviceOffline deviceOffline = new DeviceOffline();
                                                        deviceOffline.setDevice(device);
                                                        deviceOffline.setOfflineDate(new Date());
                                                        deviceOfflineRepository.save(deviceOffline);
                                                        if (null!=monitorDevice) {
                                                            monitorDevice.setOnline(0);
                                                            monitorDeviceRepository.save(monitorDevice);
                                                        }
                                                        roomOffline+=1;
                                                    }else {
						                                currentDeviceOnLine = true;
                                                        if (null!=monitorDevice) {
                                                            monitorDevice.setOnline(1);
                                                            monitorDeviceRepository.save(monitorDevice);
                                                        }
                                                        roomOnline+=1;
                                                    }
                                                }else {
						    logger.info("Scan Device id: " + device.getId() + ", no inspect data, judge offline");
                                                    currentDeviceOnLine = false;
                                                    roomOffline+=1;
                                                    if (null!=monitorDevice){
                                                        monitorDevice.setOnline(0);
                                                        monitorDeviceRepository.save(monitorDevice);
                                                    }
                                                }

                                                Date currentTime = new Date();
                                                long beginTimeMilisecond = currentTime.getTime() - 10*60*1000;
                                                Date beginTime = new Date(beginTimeMilisecond);
                                                if(currentDeviceOnLine){
                                                    // find current info type
                                                    DeviceInspect inspect = deviceInspectRepository.
                                                            findByInspectTypeIdAndDeviceId(bettaryInspectId, device.getId());
                                                    if(inspect == null){
                                                        // bettary inspect is not found, error
                                                        logger.info(String.format("Online Schedule: device %s-%d has no bettary inspect", device.getName(), device.getId()));
                                                    }
                                                    else{
                                                        List<InspectData> inspectDatas = inspectDataRepository.
                                                                findByDeviceIdAndDeviceInspectIdAndCreateDateAfterOrderByCreateDateDesc(device.getId(),
                                                                        inspect.getId(), beginTime);
                                                        if(inspectDatas == null || inspectDatas.size() == 0){
                                                            // no battery message
                                                            logger.info(String.format("Online Schedule: device %s-%d has no bettary message", device.getName(), device.getId()));
                                                        }
                                                        else {
                                                            boolean batteryIsDesc = true;
                                                            double lastBettaryValue = Double.parseDouble(inspectDatas.get(0).getResult());
                                                            // System.out.println("battery info size:" + inspectDatas.size());
                                                            for (InspectData batteryInspect : inspectDatas) {
                                                                double currentBettaryValue = Double.parseDouble(batteryInspect.getResult());
                                                                // System.out.println("battery info:" + batteryInspect.getResult() + ", " + batteryInspect.getCreateDate());
                                                                if (lastBettaryValue <= currentBettaryValue) {
                                                                    lastBettaryValue = currentBettaryValue;
                                                                } else {
                                                                    batteryIsDesc = false;
                                                                    break;
                                                                }
                                                            }
                                                            System.out.println(batteryIsDesc);

                                                            if (batteryIsDesc) {
                                                                // send power message
                                                                messageController.sendPowerMsg(device, inspect, currentTime);
                                                            }
                                                        }
                                                    }
                                                }
                                                else{
                                                    // send offline message
                                                    messageController.sendOfflineMsg(device, null, currentTime);
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
                                                roomScore +=(alertScore+offSocre);
                                            }
                                        room.setHighAlert(roomHighALert);
                                        room.setLowAlert(roomLowAlert);
                                        room.setOnline(roomOnline);
                                        room.setOffline(roomOffline);
                                        room.setTotal(roomOffline+roomOnline);
                                        room.setScore(deviceList.size()>0?roomScore/deviceList.size():(float)0);
                                        roomRepository.save(room);
                                        floorHighAlert += roomHighALert;
                                        floorLowAlert+=roomLowAlert;
                                        floorOnline+=roomOnline;
                                        floorOffline+=roomOffline;
                                        floorScore+=(deviceList.size()>0?roomScore/deviceList.size():(float)0);
                                    }
                                floor.setTotal(floorOffline+floorOnline);
                                floor.setOnline(floorOnline);
                                floor.setOffline(floorOffline);
                                floor.setLowAlert(floorLowAlert);
                                floor.setHighAlert(floorHighAlert);
                                floor.setScore(roomList.size()>0?floorScore/roomList.size():(float)0);
                                storeyRepository.save(floor);
                                buildHighAlert+=floorHighAlert;
                                buildLowAlert+=floorLowAlert;
                                buildOnline+=floorOnline;
                                buildOffline+=floorOffline;
                                buildScore +=(roomList.size()>0?floorScore/roomList.size():(float)0);
                            }
                        building.setTotal(buildOnline+buildOffline);
                        building.setOnline(buildOnline);
                        building.setOffline(buildOffline);
                        building.setHighAlert(buildHighAlert);
                        building.setLowAlert(buildLowAlert);
                        building.setScore(floorList.size()>0?buildScore/floorList.size():(float)0);
                        buildingRepository.save(building);
                        companyOnline+=buildOnline;
                        companyOffline+=buildOffline;
                        companyHighAlert+=buildHighAlert;
                        companyLowAlert+=buildLowAlert;
                        companyScore+=(floorList.size()>0?buildScore/floorList.size():(float)0);
                    }
                company.setTotal(companyOffline+companyOnline);
                company.setLowAlert(companyLowAlert);
                company.setHighAlert(companyHighAlert);
                company.setOnline(companyOnline);
                company.setOffline(companyOffline);
                company.setScore(buildingList.size()>0?companyScore/buildingList.size():(float)0);
                companyRepository.save(company);
            }
    }
}
