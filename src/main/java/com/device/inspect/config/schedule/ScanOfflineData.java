package com.device.inspect.config.schedule;

import com.device.inspect.Application;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    @Scheduled(cron = "0 0/3 * * * ? ")
    @Override
    public void scheduleTask() {
        FTPFile[] fileList = Application.offlineFTPStorageManager.getFileListOnDirectory("monitor");
        for(FTPFile ftpFile : fileList){
            ByteArrayOutputStream fileStream = null;
            Application.offlineFTPStorageManager.downloadFile(ftpFile.getName(), "monitor", fileStream);
            String fileString = fileStream.toString();
            String[] fileStringArray = fileString.split("\n");
            for(String string : fileStringArray){
                logger.info("file: " + string);
            }
            Application.offlineFTPStorageManager.deleteFile(ftpFile.getName(), "monitor");
        }

    }
}
