package com.vitrum.api.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void upload(String team, String taskTitle, MultipartFile multipartFile);

    byte[] downloadFile(String fileName, String taskTitle, String teamN);

    void deleteFile(String teamName, String taskTitle, String fileName);
}
