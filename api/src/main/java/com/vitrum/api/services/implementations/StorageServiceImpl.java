package com.vitrum.api.services.implementations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.repositories.BundleRepository;
import com.vitrum.api.repositories.FileRepository;
import com.vitrum.api.repositories.TaskRepository;
import com.vitrum.api.repositories.TeamRepository;
import com.vitrum.api.services.interfaces.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Value("${bucketName}")
    private String bucketName;

    @Value("${server-address}")
    private String serverAddress;

    private final AmazonS3 s3Client;
    private final FileRepository fileRepository;
    private final TeamRepository teamRepository;
    private final BundleRepository bundleRepository;
    private final TaskRepository taskRepository;

    @Override
    public void upload(String teamName, String bundleTitle, String taskTitle, MultipartFile multipartFile) {
        var task = Task.findTaskByTitleAndBundle(
                taskRepository,
                taskTitle,
                Bundle.findBundle(
                        bundleRepository,
                        Team.findTeamByName(teamRepository, teamName),
                        bundleTitle
                )
        );

        File fileObj = convertMultiPartFileToFile(multipartFile);
        String originalFilename = multipartFile.getOriginalFilename();
        String modifiedFilename = String.format("%s_%s_%s_%s",
                teamName,
                bundleTitle,
                taskTitle,
                Objects.requireNonNull(originalFilename).replaceAll("\\s", "_"));

        s3Client.putObject(new PutObjectRequest(bucketName, modifiedFilename, fileObj));

        String[] fileNameSplit = originalFilename.split("\\.");
        String fileExtension = fileNameSplit[fileNameSplit.length - 1];

        var file = com.vitrum.api.data.models.File.builder()
                .name(modifiedFilename)
                .path(String.format("%s/api/files/download/%s", serverAddress, modifiedFilename))
                .type(fileExtension)
                .task(task)
                .build();
        fileRepository.save(file);
    }

    @Override
    public byte[] downloadFile(String teamName, String bundleTitle, String taskTitle, String fileName) {
        String modifiedFilename = String.format("%s_%s_%s_%s",
                teamName,
                bundleTitle,
                taskTitle,
                fileName
        );

        S3Object s3Object = s3Client.getObject(bucketName, modifiedFilename);
        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to download the file", e);
        }
    }

    @Override
    public void deleteFile(String teamName, String bundleTitle, String taskTitle, String fileName) {
        String modifiedFilename = String.format("%s_%s_%s_%s",
                teamName,
                bundleTitle,
                taskTitle,
                fileName
        );

        s3Client.deleteObject(bucketName, modifiedFilename);
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }
}
