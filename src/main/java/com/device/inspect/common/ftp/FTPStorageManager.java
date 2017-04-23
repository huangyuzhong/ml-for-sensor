package com.device.inspect.common.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.Logger;
import com.device.inspect.common.service.FileUploadService;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zyclincoln on 4/8/17.
 */
public class FTPStorageManager implements FileUploadService {
    protected static Logger logger = LogManager.getLogger();

    final private String ftpHost;
    final private String user;
    final private String password;
    final private String urlPrefix = "ftpFile/";
    private FTPClient client = null;

    public FTPStorageManager(Map<String, String> config){
        this.ftpHost = config.get("ftpHost");
        this.user = config.get("user");
        this.password = config.get("password");
    }

    public FTPFile[] getFileListOnDirectory(String path){
        client = new FTPClient();
        FTPFile[] fileList = null;
        try{
            client.connect(ftpHost);
            client.login(user, password);
            client.changeWorkingDirectory(path);
            fileList = client.listFiles();
            client.logout();
            client.disconnect();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try {
                if (client.isConnected()) {
                    client.logout();
                    client.disconnect();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        return fileList;
    }

    public void downloadFile(String filename, String path, OutputStream file){
        client = new FTPClient();
        try{
            client.connect(ftpHost);
            client.login(user, password);
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.changeWorkingDirectory(path);
            client.retrieveFile(filename, file);
            client.logout();
            client.disconnect();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                if(client.isConnected()){
                    client.logout();
                    client.disconnect();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void deleteFile(String filename, String path){
       client = new FTPClient();
       try{
           client.connect(ftpHost);
           client.login(user, password);
           client.changeWorkingDirectory(path);
           client.deleteFile(filename);
           client.logout();
           client.disconnect();
       }
       catch(Exception e){
           e.printStackTrace();
       }
    }

    public String uploadFile(MultipartFile file, String containerName, String blobName, String oldName){
        client = new FTPClient();
        String fileName;
        try{
            List<String> foldersName = new ArrayList<>();
            foldersName.add(containerName);
            fileName = null;
            String[] subFolderName = blobName.split("/");
            if(subFolderName != null){
                foldersName.addAll(Arrays.asList(subFolderName));
                fileName = foldersName.get(foldersName.size() - 1);
                // remove last part of blob name, which is the file name;
                foldersName.remove(foldersName.size() - 1);
            }

            client.connect(ftpHost);
            client.login(user, password);
            client.setFileType(FTP.BINARY_FILE_TYPE);

            createFoldersAndGoThere(foldersName);

            client.storeFile(fileName, file.getInputStream());
            logger.info(String.format("FTP File Upload: Upload new file %s to ftp", fileName));
            fileName = urlPrefix + containerName + "/" + blobName;

            if(oldName != null) {
                if (oldName.startsWith(urlPrefix)) {
                    Integer lastFolderIndex = oldName.lastIndexOf('/');
                    String oldFileName = oldName.substring(lastFolderIndex + 1);
                    String oldFolderName = "~/" + oldName.substring(urlPrefix.length(), lastFolderIndex);
                    if (client.changeWorkingDirectory(oldFolderName)) {
                        if (client.deleteFile(oldFileName)) {
                            logger.info(String.format("FTP File Upload: succeeded deleting old file %s.", oldName));
                        } else {
                            logger.info(String.format("FTP File Upload: failed to delete file %s.", oldFileName));
                        }
                    } else {
                        logger.info(String.format("FTP File Upload: failed to enter old file's folder of %s.", oldFolderName));
                    }
                } else {
                    logger.info(String.format("FTP File Upload: old file %s is not in ftp server", oldName));
                }
            }
            else{
                logger.info("FTP File Upload: no old file, pass.");
            }
            client.logout();
            client.disconnect();
        }
        catch(Exception e){
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    private boolean createFoldersAndGoThere(List<String> folders){
        try {
            for (String folder : folders) {
                if (!client.changeWorkingDirectory(folder)) {
                    client.makeDirectory(folder);
                    if(!client.changeWorkingDirectory(folder)){
                        throw new IOException();
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
