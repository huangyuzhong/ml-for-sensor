package com.device.inspect.config.schedule;

import com.device.inspect.Application;
import com.device.inspect.common.model.device.DeviceInspect;
import com.device.inspect.controller.SocketMessageApi;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;

/**
 * Created by zyclincoln on 4/23/17.
 */
@Component
public class ScanOfflineData implements  MySchedule{
    private static final Logger logger = LogManager.getLogger(ScanOfflineData.class);

    @Autowired
    private SocketMessageApi socketMessageApi;

    @Scheduled(cron = "0 */10 * * * ? ")
    @Override
    public void scheduleTask() {
        if(Application.offlineFTPStorageManager == null){
            logger.info(String.format("Begin Scan Offline Data: Off Line FTP is not set, pass"));
            return;
        }
        int fileNum = 0;
        int availableFileNum = 0;
        int illegalFileNum = 0;
        int availableMessageNum = 0;
        long startTime = System.currentTimeMillis();

        FTPFile[] fileList = Application.offlineFTPStorageManager.getFileListOnDirectory("monitor");
        logger.info(String.format("Begin Scan Offline Data"));
        fileNum = fileList.length;
        try {
            for (FTPFile ftpFile : fileList) {
                logger.info(String.format("Begin Scan Offline Data File %s", ftpFile.getName()));

                ByteArrayOutputStream fileStream = new ByteArrayOutputStream();
                Application.offlineFTPStorageManager.downloadFile(ftpFile.getName(), "monitoring", fileStream);
                String fileString = fileStream.toString();
                String[] fileStringArray = fileString.split("\n");
                if (fileStringArray[fileStringArray.length - 1].equals("END")) {
                    availableFileNum++;
                    for (int index = 0; index < fileStringArray.length - 1; index++) {
                        DeviceInspect deviceInspect = socketMessageApi.parseInspectAndSave(fileStringArray[index]);
                        if (deviceInspect != null) {
                            availableMessageNum++;
                        }
                    }
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
