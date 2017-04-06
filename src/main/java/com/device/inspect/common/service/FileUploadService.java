package com.device.inspect.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by zyclincoln on 4/7/17.
 */
public interface FileUploadService {
    String uploadFile(MultipartFile file, String containerName, String blobName, String oldName);
}
