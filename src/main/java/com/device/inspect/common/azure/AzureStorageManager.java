package com.device.inspect.common.azure;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import java.util.Map;
import java.util.UUID;
import java.io.*;
import java.io.File;
/**
 * Created by gxu on 2/25/17.
 */
public class AzureStorageManager extends AzureServiceManager {
    private String azureStorageConnectionString;

    protected static Logger logger = LogManager.getLogger();

    private String defaultContainerName;

    public AzureStorageManager(String defaultContainerName, Map<String, String> config){
        super("storage", config);
        azureStorageConnectionString = String.format("DefaultEndpointsProtocol=http;AccountName=%s;AccountKey=%s",
                this.accountName, this.accessKey1);

        logger.info(String.format("connection string of storage is %s", azureStorageConnectionString));
        this.defaultContainerName = defaultContainerName;
        createContainer(defaultContainerName);
    }

    private CloudStorageAccount getStorageAccount(){
        try {
            StorageCredentials storageCredentials = StorageCredentialsAccountAndKey.tryParseCredentials(azureStorageConnectionString);
            URI blobUri = URI.create(String.format("http://%s.blob.%s", accountName, endPoint));
            URI blobUri2 = URI.create(String.format("http://%s-secondary.blob.%s", accountName, endPoint));

            URI tableUri = URI.create(String.format("http://%s.table.%s", accountName, endPoint));
            URI tableUri2 = URI.create(String.format("http://%s-secondary.table.%s", accountName, endPoint));

            URI fileUri = URI.create(String.format("http://%s.file.%s", accountName, endPoint));
            URI fileUri2 = URI.create(String.format("http://%s-secondary.file.%s", accountName, endPoint));

            URI queueUri = URI.create(String.format("http://%s.queue.%s", accountName, endPoint));
            URI queueUri2 = URI.create(String.format("http://%s-secondary.queue.%s", accountName, endPoint));


            StorageUri storageUriBlob = new StorageUri(blobUri, blobUri2);
            StorageUri storageUriFile = new StorageUri(fileUri, fileUri2);
            StorageUri storageUriTable = new StorageUri(tableUri, tableUri2);
            StorageUri storageUriQueue = new StorageUri(queueUri, queueUri2);
            CloudStorageAccount storageAccount = new CloudStorageAccount(storageCredentials, storageUriBlob, storageUriQueue, storageUriTable, storageUriFile);

            return storageAccount;
        }catch (Exception e){
            logger.error(String.format("cannot get storage account for %s, %s", accountName, e.toString()));
            return null;
        }


    }

    public CloudBlobContainer getContainer(String name){
        try
        {
            // Retrieve storage account from connection-string.


            //CloudStorageAccount storageAccount = CloudStorageAccount.parse(azureStorageConnectionString);
            CloudStorageAccount storageAccount = getStorageAccount();
            logger.info(String.format("Storage Account Blob Endpoint: %s, Uri %s",
                    storageAccount.getBlobEndpoint(),
                    storageAccount.getBlobStorageUri()));

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Get a reference to a container.
            // The container name must be lower case
            CloudBlobContainer container = blobClient.getContainerReference(name);

            return container;
        }
        catch (Exception e)
        {
            // Output the stack trace.
            logger.error(String.format("Failed to create blob storage container %s, %s", name, e.toString()));
            e.printStackTrace();
            return null;
        }
    }

    public CloudBlobContainer createContainer(String name){
        CloudBlobContainer container = getContainer(name);

        if(container != null) {
            try {
                 // Create the container if it does not exist.
                container.createIfNotExists();

                setContainerAccessiblility(container, BlobContainerPublicAccessType.BLOB);

            } catch (Exception e) {
                // Output the stack trace.
                logger.error(String.format("Failed to create blob storage container %s, %s", name, e.toString()));
                e.printStackTrace();
                return null;
            }
        }
        return container;
    }

    public void setContainerAccessiblility(CloudBlobContainer container, BlobContainerPublicAccessType accessType){
        try{
            // Create a permissions object.
            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

            // Include public access in the permissions object.
            containerPermissions.setPublicAccess(accessType);

            // Set the permissions on the container.
            container.uploadPermissions(containerPermissions);

        }catch (Exception e){
            logger.error(String.format("Failed to set container accessibility %s, %s", accessType, e.toString()));
        }
    }

    public void setContainerAccessibility(String name, BlobContainerPublicAccessType accessType){
        CloudBlobContainer container = getContainer(name);
        if(container != null) {
            setContainerAccessiblility(container, accessType);

        }
    }

    public void uploadBlobToContainer(String containerName, String filePath){
        CloudBlobContainer container = getContainer(containerName);
        if(container != null) {
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(UUID.randomUUID().toString());
                File source = new File(filePath);
                blob.upload(new FileInputStream(source), source.length());
            } catch (Exception e) {
                logger.error(String.format("Failed to upload file %s to blob container %s, %s", filePath, containerName, e.toString()));
            }
        }
    }

    public boolean deleteBlobFromContainer(String containerName, String blobName){
        CloudBlobContainer container = getContainer(containerName);
        if(container != null){
            try{
                CloudBlockBlob blob = container.getBlockBlobReference(blobName);
                blob.deleteIfExists();
                return true;
            }
            catch(Exception e){
                logger.error(String.format("Failed to delete file %s from blob container %s, %s", blobName, containerName, e.toString()));
                return false;
            }
        }
        else{
            return false;
        }
    }

    public String uploadBlobToContainer(String containerName, MultipartFile file, String blobName){
        //CloudBlobContainer container = getContainer(containerName);
        CloudBlobContainer container = createContainer(containerName);
        if(container != null) {
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(blobName);

                blob.upload(file.getInputStream(), file.getSize());

                return String.format("https://%s.blob.%s/%s/%s", accountName, endPoint, containerName, blobName);
            } catch (Exception e) {
                logger.error(String.format("Failed to upload file %s to blob container %s, %s", file.getName(), containerName, e.toString()));
                return null;
            }
        }else{
            return null;
        }
    }


    public String uploadBlobToDefaultContainer(MultipartFile file, String blobName){
        logger.info(String.format("get container %s", this.defaultContainerName));
        CloudBlobContainer container = getContainer(this.defaultContainerName);
        if(container != null) {
            try {
                CloudBlockBlob blob = container.getBlockBlobReference(blobName);

                logger.info(String.format("uploading blob %s", blobName));
                blob.upload(file.getInputStream(), file.getSize());

                return String.format("https://%s.blob.%s/%s/%s", accountName, endPoint, this.defaultContainerName, blobName);
            } catch (Exception e) {
                logger.error(String.format("Failed to upload file %s to blob container %s, %s", file.getName(), this.defaultContainerName, e.toString()));
                return null;
            }
        }else{
            return null;
        }
    }

}
