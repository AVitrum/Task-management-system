package com.vitrum.api.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void upload(Long team, Long task, MultipartFile multipartFile);

    byte[] downloadFile(Long task, Long team, String fileName);

    void deleteFile(Long team, Long task, String fileName);
}
