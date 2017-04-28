package com.device.inspect.config.schedule;

import com.device.inspect.Application;
import com.device.inspect.common.model.device.Device;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.common.model.device.MonitorDevice;
import com.device.inspect.common.model.record.OfflineHourUnit;
import com.device.inspect.common.repository.device.DeviceInspectRepository;
import com.device.inspect.common.repository.device.MonitorDeviceRepository;
import com.device.inspect.common.service.OfflineHourQueue;
import com.device.inspect.controller.SocketMessageApi;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by zyclincoln on 4/23/17.
 */
@Component
public class ScanOfflineData implements  MySchedule{
    private static final Logger logger = LogManager.getLogger(ScanOfflineData.class);

    @Autowired
    private DeviceInspectRepository deviceInspectRepository;

    @Autowired
    private MonitorDeviceRepository monitorDeviceRepository;

    @Autowired
    private SocketMessageApi socketMessageApi;

    @Autowired
    private OfflineHourQueue requestQueue;

    @Scheduled(cron = "0 */10 * * * ? ")
    @Override
    public void scheduleTask() {
        if(Application.offlineFTPStorageManager == null){
            logger.info(String.format("Begin Scan Offline Data: Off Line FTP is not set, pass   "));
            return;
        }
        int fileNum = 0;
        int availableFileNum = 0;
        int illegalFileNum = 0;
        int availableMessageNum = 0;
        long startTime = System.currentTimeMillis();

        FTPFile[] fileList = Application.offlineFTPStorageManager.getFileListOnDirectory("monitoring");
        logger.info(String.format("Begin Scan Offline Data"));
        fileNum = fileList.length;
        try {
            for (FTPFile ftpFile : fileList) {
                logger.info(String.format("Begin Scan Offline Data File %s", ftpFile.getName()));

                Long timeStamp = Long.valueOf(ftpFile.getName().substring(ftpFile.getName().lastIndexOf('-') + 1));
                String monitorCode = ftpFile.getName().substring(0, ftpFile.getName().indexOf('-'));
                Device device = null;
                Date beginTime = null;
                Date endTime = null;
                if(timeStamp > 0){
                    beginTime = new Date(timeStamp);
                    endTime = new Date(beginTime.getTime() + 60*60*1000);
                    MonitorDevice monitorDevice = monitorDeviceRepository.findByNumber(monitorCode);
                    if(monitorDevice != null){
                        device = monitorDevice.getDevice();
                    }
                }

                ByteArrayOutputStream fileStream = new ByteArrayOutputStream();
                Application.offlineFTPStorageManager.downloadFile(ftpFile.getName(), "monitoring", fileStream);
                String fileString = fileStream.toString();
                String[] fileStringArray = fileString.split("\n");
                if (fileStringArray[fileStringArray.length - 1].equals("END") && beginTime != null && device != null) {
                    availableFileNum++;
                    for (int index = 0; index < fileStringArray.length - 1; index++) {
                        DeviceInspect deviceInspect = socketMessageApi.parseInspectAndSave(fileStringArray[index]);
                        if (deviceInspect != null) {
                            availableMessageNum++;
                        }
                    }
                    requestQueue.recalculateRequest.add(new OfflineHourUnit(beginTime, endTime, device));
                    logger.info(String.format("Scan Offline Data: add recalculate request of device %d from %s to %s to queue."),
                            device.getId(), beginTime.toString(), endTime.toString());
                } else {
                    illegalFileNum++;
                    logger.info(String.format("File Tail of Offline Data File %s is illegal, pass.", ftpFile.getName()));
                }
                Application.offlineFTPStorageManager.deleteFile(ftpFile.getName(), "monitoring");
                fileStream.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        long endTIme = System.currentTimeMillis();
        logger.info(String.format("Scan Offline Data Summary:\n  total file: %d\n  available file: %d\n  illegal file: %d\n  total storaged message: %d\n  Use Time: %d sec",
                fileNum, availableFileNum, illegalFileNum, availableMessageNum, (endTIme - startTime)/1000));
        logger.info(String.format("End Scan Offline Data"));
    }
}