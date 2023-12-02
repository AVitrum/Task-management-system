package com.vitrum.api.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void upload(MultipartFile multipartFile);

    byte[] downloadFile(String fileName);

    void deleteFile(String fileName);
}
