package com.device.inspect.config.schedule;

import com.device.inspect.Application;
import com.device.inspect.common.cache.MemoryDevice;
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
import com.device.inspect.common.service.MemoryCacheDevice;
import com.device.inspect.common.managers.MessageController;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2016/10/18.
 */
@Component()
public class MyDeviceStatusScheduleImp{
    private static final Logger logger = LogManager.getLogger(MyDeviceStatusScheduleImp.class);

    @Autowired
    private DeviceRepository deviceRepository;

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
    private InspectTypeRepository inspectTypeRepository;

    @Autowired
    private MessageController messageController;

    @Autowired
    private MemoryCacheDevice memoryCacheDevice;

    static private Integer bettaryInspectId = 9;

    /**
     * 从当前时间开始定时每隔10分钟刷新一次
     * 刷新设备的高级报警数量，低级报警数量，在线数量，掉线数量
     * 刷新室的高级报警数量，低级报警数量，在线数量，掉线数量
     * 刷新层的高级报警数量，低级报警数量，在线数量，掉线数量
     * 刷新楼的高级报警数量，低级报警数量，在线数量，掉线数量
     * 刷新公司的高级报警数量，低级报警数量，在线数量，掉线数量
     */

//    @Scheduled(cron = "0 0/5 * * * ?")
    public void executeInternal(){

        final int CONTINUOUS_OFFLINE_THRESHOLD = 10; // 判断是否为持续性offline的阈值 (分钟)

        if (Application.isTesting){
            return;
        }
        logger.info("Start schedule to summarize device status");
        Date scheduleStartTime = new Date();
        Date time5minBefore = DateUtils.addMinutes(scheduleStartTime, -5);
        Date time10minBefore = DateUtils.addMinutes(scheduleStartTime, -10);

        InspectType onlineInspect = inspectTypeRepository.findByCode("-1");

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
                if (null!=buildingList){
                    for (Building building : buildingList){

                        Integer buildLowAlert = 0;
                        Integer buildHighAlert = 0;
                        Integer buildOnline = 0;
                        Integer buildOffline = 0;
                        Float buildScore = (float)0;
                        List<Storey> floorList = storeyRepository.findByBuildIdAndEnable(building.getId(),1);
                        if (null!=floorList){
                            for (Storey floor : floorList){

                                Integer floorLowAlert = 0;
                                Integer floorHighAlert = 0;
                                Integer floorOnline = 0;
                                Integer floorOffline = 0;
                                Float floorScore = (float)0;
                                List<Room> roomList = roomRepository.findByFloorIdAndEnable(floor.getId(),1);
                                if (null!=roomList){
                                    for (Room room:roomList){

                                        Integer roomLowAlert = 0;
                                        Integer roomHighAlert = 0;
                                        Integer roomOnline = 0;
                                        Integer roomOffline = 0;
                                        Float roomScore = (float)0;


                                        Integer roomDeviceCount = deviceRepository.countByRoomIdAndEnable(room.getId(), 1);

                                        if(roomDeviceCount <= 0){
                                            continue;
                                        }


                                        List<Device> roomDeviceList = deviceRepository.findByRoomIdAndEnable(room.getId(), 1);

                                        for(Device device: roomDeviceList){
                                            Date scanDeviceStartTime = new Date();
                                            List<String> inspectTypes = new ArrayList<String>();
                                            List<DeviceInspect> inspectList = deviceInspectRepository.findByDeviceId(device.getId());

                                            for(DeviceInspect deviceInspect: inspectList){
                                                //inspectTypes.add(InspectProcessTool.getMeasurementByCode(deviceInspect.getInspectType().getCode()));
                                                inspectTypes.add(deviceInspect.getInspectType().getMeasurement());
                                            }

                                            Date startTimeScanAlert = new Date();
                                            // 统计该房间内近5分钟内有报警的设备数量
                                            // TODO: deal with multiple instance situation, read status from db
                                            int alert_type = 0;
                                            Date latestAlertTime = null;
                                            MemoryDevice memoryDevice = memoryCacheDevice.get(device.getId());
                                            if(memoryDevice != null){
                                                logger.debug(String.format("Device scan: device %d is found in cache, use cached data", device.getId()));
                                                if(memoryDevice.getLastAlertTime() != null && memoryDevice.getLastAlertTime().getTime() - time5minBefore.getTime() > 0){
                                                    if(memoryDevice.getLastAlertType() == 1){
                                                        alert_type = 1;
                                                        latestAlertTime = memoryDevice.getLastAlertTime();
                                                    }
                                                    else if(memoryDevice.getLastAlertType() == 2){
                                                        alert_type = 2;
                                                        latestAlertTime = memoryDevice.getLastAlertTime();
                                                    }
                                                    else if(memoryDevice.getLastAlertType() == 0){
                                                        alert_type = 0;
                                                    }
                                                    else{
                                                        logger.warn(String.format("Device scan: unknown alert type %d.", memoryDevice.getLastAlertType()));
                                                    }
                                                }
                                            }
                                            else{
                                                logger.debug(String.format("Device scan: device %d is not found in cache, use influxdb data, device alert time may be not precise.", device.getId()));
                                                // use data in influxdb
                                                if(Application.influxDBManager.countDeviceTotalAlertByTime(inspectTypes, device.getId(), "high", time5minBefore, scheduleStartTime) > 0){
                                                    alert_type = 2;
                                                    latestAlertTime = time5minBefore;
                                                }
                                                else if(Application.influxDBManager.countDeviceTotalAlertByTime(inspectTypes, device.getId(), "low", time5minBefore, scheduleStartTime) > 0){
                                                    alert_type = 1;
                                                    latestAlertTime = time5minBefore;
                                                }
                                                else{
                                                    alert_type = 0;
                                                }
                                            }

                                            if(alert_type == 1){
                                                roomLowAlert ++;
                                                device.setLastYellowAlertTime(latestAlertTime);
                                            }
                                            else if(alert_type == 2){
                                                roomHighAlert ++;
                                                device.setLastRedAlertTime(latestAlertTime);
                                            }
                                            device.setStatus(alert_type);

                                            Date startTimeScanDeviceOnline = new Date();

                                            // 统计该房间内近5分钟内离线的设备数量
                                            MonitorDevice monitor = device.getMonitorDevice();

                                            boolean deviceOnline = false;
                                            if(memoryDevice != null && memoryDevice.getLastActivityTime() != null){
                                                logger.debug(String.format("Device scan: device %d is found in cache, use cached data", device.getId()));
                                                if(memoryDevice.getLastActivityTime().getTime() - time5minBefore.getTime() > 0){
                                                    deviceOnline = true;
                                                }
                                                device.setLastActivityTime(memoryDevice.getLastActivityTime());
                                            }
                                            else{
                                                logger.debug(String.format("Device scan: device %d is not found in cache, use influxdb data", device.getId()));
                                                Integer countMonitorDataIn5min = Application.influxDBManager.countDeviceTotalTelemetryByTime(inspectTypes, device.getId(), time5minBefore, scheduleStartTime);
                                                if(countMonitorDataIn5min > 0){
                                                    deviceOnline = true;
                                                }
                                                device.setLastActivityTime(time5minBefore);
                                            }

                                            deviceRepository.save(device);

                                            if(!deviceOnline){

                                                roomOffline ++;
                                                if(monitor.getOnline() != 0){
                                                    monitor.setOnline(0);
                                                    monitorDeviceRepository.save(monitor);
                                                }

                                                DeviceOffline deviceOffline = new DeviceOffline();
                                                deviceOffline.setDevice(device);
                                                deviceOffline.setOfflineDate(scheduleStartTime);
                                                deviceOfflineRepository.save(deviceOffline);

                                                if(device.getId() == 515){
                                                    logger.info("this is device " + device.getName());
                                                }
                                                AlertCount latestAlert = alertCountRepository.findTopByDeviceIdAndInspectTypeIdOrderByCreateDateDesc(device.getId(), onlineInspect.getId());

                                                boolean isNewAlert = false;
                                                if(latestAlert == null){
                                                    isNewAlert = true;
                                                }

                                                else if(scheduleStartTime.getTime() - latestAlert.getFinish().getTime() > CONTINUOUS_OFFLINE_THRESHOLD*60*1000){
                                                    isNewAlert = true;
                                                }

                                                if(isNewAlert){
                                                    latestAlert = AlertCount.createNewAlertAndSave(alertCountRepository, device, onlineInspect, 1, "s", scheduleStartTime);
                                                    logger.info(String.format("New offline alert %d created for device %d", latestAlert.getId(), device.getId()));
                                                }else{
                                                    latestAlert.setFinish(scheduleStartTime);
                                                    alertCountRepository.save(latestAlert);
                                                }

                                                messageController.sendOfflineMsg(device, latestAlert, scheduleStartTime);

                                            }else {
                                                // 5分钟内有数据， 则认为在线
                                                roomOnline ++;
                                                if(monitor.getOnline() != 1){
                                                    monitor.setOnline(1);
                                                    monitorDeviceRepository.save(monitor);
                                                }

                                                // 10 分钟内电池电量有没有下降
                                                logger.info("Scan online Device id: " + device.getId());
                                                DeviceInspect batteryInspect = deviceInspectRepository.
                                                        findByInspectTypeIdAndDeviceId(bettaryInspectId, device.getId());
                                                if(batteryInspect == null){
                                                    // bettary inspect is not found, error
                                                    logger.debug(String.format("Online Schedule: device %s-%d has no battery inspect", device.getName(), device.getId()));
                                                }
                                                else{
                                                    List<List<Object>> recentBatteryData = Application.influxDBManager.readTelemetryInTimeRange(
                                                            batteryInspect.getInspectType().getMeasurement(),
                                                            device.getId(),
                                                            batteryInspect.getId(),
                                                            time10minBefore,
                                                            scheduleStartTime,
                                                            Calendar.SECOND
                                                            );

                                                    if(recentBatteryData == null || recentBatteryData.size()==0){
                                                        logger.info(String.format("Online Schedule: device %s-%d has no battery message", device.getName(), device.getId()));

                                                    }
                                                    else{
                                                        List<Object> latestBatteryData = recentBatteryData.get(recentBatteryData.size()-1);
                                                        Double lastBatteryValue = (Double)latestBatteryData.get(1);
                                                        boolean batteryIsDesc = true;


                                                        for(int i=1; i<recentBatteryData.size(); i++){
                                                            Double batteryValue = (Double)recentBatteryData.get(i).get(1);
                                                            if(batteryValue < lastBatteryValue){
                                                                batteryIsDesc = false;
                                                                break;
                                                            }
                                                            else{
                                                                lastBatteryValue = batteryValue;
                                                            }
                                                        }
                                                        if (batteryIsDesc) {
                                                            // send power message
                                                            AlertCount alert = AlertCount.createNewAlertAndSave(alertCountRepository, device, batteryInspect.getInspectType(), 1, "s", scheduleStartTime);
                                                            logger.info(String.format("New battery alert %d created for device %d", alert.getId(), device.getId()));
                                                            messageController.sendPowerMsg(device, alert, scheduleStartTime);
                                                        }
                                                    }

                                                }
                                            }


                                            Date startTimeScanScore = new Date();

                                            // 计算设备健康度，
                                            // TODO: 此处可以优化， 在时间没有跨日的情况下， 很多计算是不需要的。 等整个健康值计算方式确定之后在这个地方一起修改
                                            Float offSocre = (float)50.0;
                                            Float alertScore = (float) 50.0;
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-1);
                                            Date oneDayBefore = calendar.getTime();
                                            calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-7);
                                            Date oneWeekBefore = calendar.getTime();
                                            calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-30);
                                            Date oneMonthBefore = calendar.getTime();

                                            // get offline minutes

                                            List<BigDecimal> offlineMinutesOneDay = alertCountRepository.findAlertSumDurationByCreateDateBetweenAndInspectTypeIdAndDeviceId(oneDayBefore, new Date(), onlineInspect.getId(), device.getId());
                                            List<BigDecimal> offlineMinutesOneWeek = alertCountRepository.findAlertSumDurationByCreateDateBetweenAndInspectTypeIdAndDeviceId(oneWeekBefore, oneDayBefore, onlineInspect.getId(), device.getId());
                                            List<BigDecimal> offlineMinutesOneMonth = alertCountRepository.findAlertSumDurationByCreateDateBetweenAndInspectTypeIdAndDeviceId(oneMonthBefore, oneWeekBefore, onlineInspect.getId(), device.getId());

                                            Long oneDayOffline = offlineMinutesOneDay != null && offlineMinutesOneDay.size() > 0 && offlineMinutesOneDay.get(0) != null ? offlineMinutesOneDay.get(0).longValue() : 0;
                                            Long oneWeekOffline = offlineMinutesOneWeek != null && offlineMinutesOneWeek.size() > 0 && offlineMinutesOneWeek.get(0) != null ? offlineMinutesOneWeek.get(0).longValue() : 0;
                                            Long oneMonthOffline = offlineMinutesOneMonth != null && offlineMinutesOneMonth.size() > 0 && offlineMinutesOneMonth.get(0) != null ? offlineMinutesOneMonth.get(0).longValue() : 0;

                                            // every 3 offline minutes considered as one offline unit

                                            offSocre =(float) (offSocre-oneDayOffline/3-oneWeekOffline/3*0.5-oneMonthOffline/3*0.1);
                                            offSocre = offSocre>0?offSocre:0;

                                            Long oneHighAlert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                    2,oneDayBefore,new Date());
                                            Long oneLowALert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                    1,oneDayBefore,new Date());
                                            Long sevenHighALert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                    2,oneWeekBefore,oneDayBefore);
                                            Long sevenLowALert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                    1,oneWeekBefore,oneDayBefore);
                                            Long thirtyHighALert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                    2,oneMonthBefore,oneWeekBefore);
                                            Long thirtyLowALert = alertCountRepository.countByDeviceIdAndTypeAndCreateDateBetween(device.getId(),
                                                    1,oneMonthBefore,oneWeekBefore);
                                            alertScore = (float)(alertScore-oneHighAlert-sevenHighALert*0.5-thirtyHighALert*0.25-
                                                    oneLowALert*0.5-sevenLowALert*0.25-thirtyLowALert*0.1);
                                            alertScore = alertScore>0?alertScore:0;
                                            device.setScore(String.valueOf(offSocre+alertScore));
                                            deviceRepository.save(device);
                                            roomScore +=(alertScore+offSocre);

                                            Date scanDeviceEndTime = new Date();

                                            long timeCostGetInfo = startTimeScanAlert.getTime() - scanDeviceStartTime.getTime();

                                            long timeCostAlert = startTimeScanDeviceOnline.getTime() - startTimeScanAlert.getTime();

                                            long timeCostOnline = startTimeScanScore.getTime() - startTimeScanDeviceOnline.getTime();

                                            long timeCostScore = scanDeviceEndTime.getTime() - startTimeScanScore.getTime();

                                            long timeCost = scanDeviceEndTime.getTime() - scanDeviceStartTime.getTime();

                                            logger.debug(String.format("Scan device %d taks %d on get inspect, %d on alert, %d on online, %d on score, %d ms total", device.getId(), timeCostGetInfo, timeCostAlert, timeCostOnline, timeCostScore, timeCost));
                                        }

                                        logger.debug(String.format("Room %d has %d device reports Yellow alert in 5 minutes", room.getId(), roomLowAlert));
                                        logger.debug(String.format("Room %d has %d device reports Red alert in 5 minutes", room.getId(), roomHighAlert));

                                        logger.debug(String.format("Room %d has %d online device", room.getId(), roomOnline));

                                        logger.debug(String.format("Room %d has %d offline device", room.getId(), roomOffline));

                                        room.setHighAlert(roomHighAlert);
                                        room.setLowAlert(roomLowAlert);
                                        room.setOnline(roomOnline);
                                        room.setOffline(roomOffline);
                                        room.setTotal(roomOffline+roomOnline);

                                        roomScore = roomDeviceList.size()>0?roomScore/roomDeviceList.size():(float)0;
                                        room.setScore(roomScore);
                                        roomRepository.save(room);
                                        floorHighAlert += roomHighAlert;
                                        floorLowAlert+=roomLowAlert;
                                        floorOnline+=roomOnline;
                                        floorOffline+=roomOffline;
                                        floorScore+=roomScore;
                                    }
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
                }
                company.setTotal(companyOffline+companyOnline);
                company.setLowAlert(companyLowAlert);
                company.setHighAlert(companyHighAlert);
                company.setOnline(companyOnline);
                company.setOffline(companyOffline);
                company.setScore(buildingList.size()>0?companyScore/buildingList.size():(float)0);
                companyRepository.save(company);
            }

        Date scheduleEndTime = new Date();

        long timeCost = scheduleEndTime.getTime()-scheduleStartTime.getTime();

        logger.info(String.format("--- Schedule update-device-status takes %d ms", timeCost));
    }
}
