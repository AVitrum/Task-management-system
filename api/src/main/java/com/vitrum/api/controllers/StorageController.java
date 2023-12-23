package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/{team}/{task}/files")
@RequiredArgsConstructor
@Validated
public class StorageController {

    private final StorageService service;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @PathVariable Long team,
            @PathVariable Long task,
            @RequestParam(value = "file") MultipartFile file
    ) {
        service.upload(team, task, file);
        return ResponseEntity.ok("Uploaded");
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadFile(
            @PathVariable Long team,
            @PathVariable Long task,
            @PathVariable String fileName
    ) {
        byte[] data = service.downloadFile(team, task, fileName);
        ByteArrayResource resource = new ByteArrayResource(data);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.setContentLength(data.length);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<String> deleteFile(
            @PathVariable Long team,
            @PathVariable Long task,
            @PathVariable String fileName
    ) {
        service.deleteFile(team, task, fileName);
        return ResponseEntity.ok("File deleted successfully");
    }
}
