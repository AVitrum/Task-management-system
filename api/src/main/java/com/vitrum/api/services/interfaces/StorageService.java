package com.vitrum.api.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void upload(String team, String bundle, String task, MultipartFile multipartFile);

    byte[] downloadFile(String fileName, String bundle, String task, String teamN);

    void deleteFile(String teamName, String bundleTitle, String taskTitle, String fileName);
}
