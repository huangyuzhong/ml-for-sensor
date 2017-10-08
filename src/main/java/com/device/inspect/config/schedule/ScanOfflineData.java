package com.device.inspect.config.schedule;

import com.device.inspect.Application;
import com.device.inspect.common.model.device.*;
import com.device.inspect.common.model.record.OfflineHourUnit;
import com.device.inspect.common.repository.device.AlertCountRepository;
import com.device.inspect.common.repository.device.DeviceInspectRepository;
import com.device.inspect.common.repository.device.MonitorDeviceRepository;
import com.device.inspect.common.service.OfflineHourQueue;
import com.device.inspect.controller.SocketMessageApi;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by zyclincoln on 4/23/17.
 */
@Component
public class ScanOfflineData{
    private static final Logger logger = LogManager.getLogger(ScanOfflineData.class);

    @Autowired
    private DeviceInspectRepository deviceInspectRepository;

    public void setDeviceInspectRepository(DeviceInspectRepository deviceInspectRepository){
        this.deviceInspectRepository = deviceInspectRepository;
    }

    @Autowired
    private MonitorDeviceRepository monitorDeviceRepository;

    public void setMonitorDeviceRepository(MonitorDeviceRepository monitorDeviceRepository){
        this.monitorDeviceRepository = monitorDeviceRepository;
    }

    @Autowired
    private SocketMessageApi socketMessageApi;

    public void setSocketMessageApi(SocketMessageApi socketMessageApi){
        this.socketMessageApi = socketMessageApi;
    }

    @Autowired
    private OfflineHourQueue requestQueue;

    public void setRequestQueue(OfflineHourQueue requestQueue){
        this.requestQueue = requestQueue;
    }

    @Autowired
    private AlertCountRepository alertCountRepository;

    public void setAlertCountRepository(AlertCountRepository alertCountRepository){
        this.alertCountRepository = alertCountRepository;
    }

//    @Scheduled(cron = "0 */10 * * * ?")
    public void executeInternal(){
        if(Application.offlineFTPStorageManager == null){
            logger.info(String.format("Begin Scan Offline Data: Off Line FTP is not set, pass   "));
            return;
        }
        if(Application.isTesting){
            return;
        }
        int fileNum = 0;
        int availableFileNum = 0;
        int illegalFileNum = 0;
        int availableMessageNum = 0;
        long startTime = System.currentTimeMillis();
        FTPFile[] fileList = null;

        try {
            fileList = Application.offlineFTPStorageManager.getFileListOnDirectory("monitoring");

        }catch (Exception e) {
            logger.error(String.format("Failed to get file from ftp server: ", e.toString()));
            e.printStackTrace();
            return;
        }

        if(fileList == null){
            logger.info("Offline file list is null, skip");
            return;
        }

        try{
            logger.info(String.format("Begin Scan Offline Data"));
            fileNum = fileList.length;

            for (FTPFile ftpFile : fileList) {
                logger.info(String.format("Begin Scan Offline Data File %s", ftpFile.getName()));

                Long timeStamp = Long.valueOf(ftpFile.getName().substring(ftpFile.getName().lastIndexOf('-') + 1));

                // the ftp offline data file
                String monitorCode = ftpFile.getName().substring(0, ftpFile.getName().indexOf('-'));
                Device device = null;
                Date beginTime = null;
                Date endTime = null;
                if(timeStamp > 0){
                    beginTime = new Date(timeStamp);
                    endTime = new Date(beginTime.getTime() + 60*60*1000);
                    MonitorDevice monitorDevice = monitorDeviceRepository.findByNumber(monitorCode);
                    // 这里的逻辑在单终端对应多设备之后需要修改, 每一个ftp 文件里可能会有多个设备的数据.
                    // 需要把从文件里读出的数据按照设备id group起来.
                    if(monitorDevice != null){
                        device = monitorDevice.getDevice();
                    }
                }

                ByteArrayOutputStream fileStream = new ByteArrayOutputStream();
                Application.offlineFTPStorageManager.downloadFile(ftpFile.getName(), "monitoring", fileStream);
                String fileString = fileStream.toString();
                String[] fileStringArray = fileString.split("\n");
                if (fileStringArray[fileStringArray.length - 1].equals("END") && beginTime != null && device != null) {
                    logger.info(String.format("Scan Offline Data: device %d, begin time %s, end time %s, monitor %s.",
                            device.getId(), beginTime.toString(), endTime.toString(), monitorCode));

                    availableFileNum++;
                    for (int index = 0; index < fileStringArray.length - 1; index++) {
                        DeviceInspect deviceInspect = socketMessageApi.parseAndProcessInspectMessage(fileStringArray[index], false);
                        if (deviceInspect != null) {
                            availableMessageNum++;
                        }
                    }

                    logger.info(String.format("%d Offline data of device %d is added to db", fileStringArray.length, device.getId()));

                    requestQueue.recalculateRequest.add(new OfflineHourUnit(beginTime, endTime, device));
                    logger.info(String.format("Scan Offline Data: add recalculate request of device %d from %s to %s to queue.",
                            device.getId(), beginTime.toString(), endTime.toString()));
                    int mergedAlert = mergeAlertOfMonitorDevice(device.getId(),
                            new Date(beginTime.getTime() - 5*60*1000),
                            new Date(endTime.getTime() + 5*60*1000));
                    logger.info(String.format("Scan Offline Data: merged %d alert", mergedAlert));
                } else {
                    illegalFileNum++;
                    logger.info(String.format("File Tail of Offline Data File %s is illegal, pass.", ftpFile.getName()));
                }
                fileStream.close();

                // backup file
                InputStream is = new ByteArrayInputStream(fileStream.toByteArray());
                logger.info(String.format("FTP: store offline data file %s to backup folder", ftpFile.getName()));

                Application.offlineFTPStorageManager.storeFile(ftpFile.getName(), "backup", is);
                // delete original one
                logger.info(String.format("FTP: delete offline data file %s from monitoring folder", ftpFile.getName()));
                Application.offlineFTPStorageManager.deleteFile(ftpFile.getName(), "monitoring");

            }

            long endTIme = System.currentTimeMillis();
            logger.info(String.format("Scan Offline Data Summary:\n  total file: %d\n  available file: %d\n  illegal file: %d\n  total storaged message: %d\n  Use Time: %d sec",
                    fileNum, availableFileNum, illegalFileNum, availableMessageNum, (endTIme - startTime)/1000));
            logger.info(String.format("End Scan Offline Data"));
        }
        catch(Exception e){
            e.printStackTrace();
            logger.error(String.format("Scan Offline data failed due to error %s", e.toString()));

        }
    }

    // merge alert after insert offline data
    int mergeAlertOfMonitorDevice(Integer deviceId, Date beginTime, Date endTime){
        List<DeviceInspect> deviceInspects = deviceInspectRepository.findByDeviceId(deviceId);
        int mergedAlert = 0;
        for(DeviceInspect deviceInspect : deviceInspects){
            List<AlertCount> alertCounts = alertCountRepository.findByDeviceIdAndInspectTypeIdAndFinishAfterAndCreateDateBeforeOrderByFinishAsc(
                    deviceId, deviceInspect.getInspectType().getId(), beginTime, endTime);
            for(int index = 0; index < alertCounts.size(); index++){
                // merge when adjacent alerts have same type and interval is less than 2 minutes
                if(index + 1 < alertCounts.size() &&
                        alertCounts.get(index).getType() == alertCounts.get(index+1).getType() &&
                        (alertCounts.get(index+1).getCreateDate().getTime() - alertCounts.get(index).getFinish().getTime()) < 2*60*1000){

                    // check inspect data in this time period which is not same alert type
                    // if got such data, we don't merge
                    int originalAlertType = alertCounts.get(index).getType();
                    String alertStatusString = originalAlertType == 2 ? "high" : " low";
                    int countOtherStatus = Application.influxDBManager.countDeviceNotCertainStatusByTime(
                            deviceInspect.getInspectType().getMeasurement(),
                            //InspectProcessTool.getMeasurementByCode(deviceInspect.getInspectType().getCode()),
                            deviceId, alertStatusString, alertCounts.get(index).getFinish(), alertCounts.get(index+1).getCreateDate());

                    if (countOtherStatus > 0){
                        continue;
                    }

                    alertCounts.get(index).setFinish(alertCounts.get(index+1).getFinish());
                    // update merged alert
                    alertCountRepository.save(alertCounts.get(index));
                    // erase redundant alert
                    alertCountRepository.delete(alertCounts.get(index+1));

                    logger.info(String.format("Merge Alert: device id: %d, merge alert id %d, finish at %s and id %d, start at %s into %d",
                            deviceId, alertCounts.get(index).getId(), alertCounts.get(index).getFinish().toString(),
                            alertCounts.get(index+1).getId(), alertCounts.get(index+1).getCreateDate().toString(), alertCounts.get(index).getId()));


                    alertCounts.remove(index+1);
                    // index not move when merging, so we can merge it to the next alert if necessary
                    index--;
                    mergedAlert++;
                }
            }
        }
        return mergedAlert;
    }
}
