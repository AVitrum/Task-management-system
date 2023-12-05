package com.vitrum.api.controllers;

import com.amazonaws.services.s3.model.AmazonS3Exception;
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
@RequestMapping("/api/teams/{team}/bundles/{bundle}/tasks/{task}")
@RequiredArgsConstructor
@Validated
public class StorageController {

    private final StorageService service;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @PathVariable String team,
            @PathVariable String bundle,
            @PathVariable String task,
            @RequestParam(value = "file") MultipartFile file
    ) {
        service.upload(team, bundle, task, file);
        return ResponseEntity.ok("Uploaded");
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(
            @PathVariable String team,
            @PathVariable String bundle,
            @PathVariable String task,
            @PathVariable String fileName
    ) {
        try {
            byte[] data = service.downloadFile(team, bundle, task, fileName);
            ByteArrayResource resource = new ByteArrayResource(data);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.setContentLength(data.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (AmazonS3Exception | IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("File not found");
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(
            @PathVariable String team,
            @PathVariable String bundle,
            @PathVariable String task,
            @PathVariable String fileName
    ) {
        try {
            service.deleteFile(team, bundle, task, fileName);
            return ResponseEntity.ok("File deleted successfully");
        } catch (AmazonS3Exception | IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("File not found");
        }
    }
}
