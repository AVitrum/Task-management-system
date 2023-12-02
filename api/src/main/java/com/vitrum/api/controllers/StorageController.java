package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Validated
public class StorageController {

    private final StorageService service;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam(value = "file") MultipartFile file
    ) {
        service.upload(file);
        return ResponseEntity.ok("Uploaded");
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        try {
            byte[] data = service.downloadFile(fileName);
            ByteArrayResource resource = new ByteArrayResource(data);
            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(
            @PathVariable String fileName
    ) {
        service.deleteFile(fileName);
        return ResponseEntity.ok("File deleted successfully");
    }
}
