package com.vitrum.api.services.implementations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.vitrum.api.data.enums.Role;
import com.vitrum.api.data.models.File;
import com.vitrum.api.data.models.User;
import com.vitrum.api.repositories.FileRepository;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.config.JwtService;
import com.vitrum.api.data.request.ChangeUserCredentials;
import com.vitrum.api.data.request.RegisterRequest;
import com.vitrum.api.data.response.UserProfileResponse;
import com.vitrum.api.services.interfaces.UserService;
import com.vitrum.api.util.Converter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${bucketName}")
    private String bucketName;
    @Value("${server-address}")
    private String serverAddress;

    private final UserRepository repository;
    private final FileRepository fileRepository;
    private final AuthenticationServiceImpl authenticationServiceImpl;
    private final Converter converter;
    private final JwtService jwtService;
    private final AmazonS3 s3Client;

    @Override
    public UserProfileResponse profile(HttpServletRequest request) {
        String jwt = extractJwtFromRequest(request);
        String userEmail = jwtService.extractEmail(jwt);
        User user = repository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return converter.mapUserToUserProfileResponse(user);
    }

    @Override
    public void create(RegisterRequest request) {
        authenticationServiceImpl.register(request);
        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        try {
            user.setRole(Role.valueOf(request.getRole()));
            repository.save(user);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error");
        }
    }

    @Override
    public void changeCredentials(ChangeUserCredentials request) {
        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        try {
            if (request.getRole() != null) {
                user.setRole(Role.valueOf(request.getRole()));
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getNewUsername() != null) {
                user.setUsername(request.getNewUsername());
            }
            repository.save(user);
        } catch (IllegalArgumentException e) {
            user.setRole(Role.USER);
            repository.save(user);
            throw new IllegalArgumentException("Wrong Credentials");
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("User with the same email/username already exists.");
        }
    }

    @Override
    public void changeStatus(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setIsBanned(!user.getIsBanned());
        repository.save(user);

        authenticationServiceImpl.revokeAllUserTokens(user);
    }

    @Override
    public void addImage(Principal connectedUser, MultipartFile multipartFile) {
        User user = User.getUserFromPrincipal(connectedUser);

        var fileObj = StorageServiceImpl.convertMultiPartFileToFile(multipartFile);
        String originalFilename = multipartFile.getOriginalFilename();
        String modifiedFilename = String.format("img_%s_%s",
                user.getTrueUsername(),
                Objects.requireNonNull(originalFilename).replaceAll("\\s", "_"));

        if (fileRepository.existsByName(modifiedFilename))
            throw new IllegalArgumentException("A file with this name already exists." +
                    " Delete it before adding this one");

        String[] fileNameSplit = originalFilename.split("\\.");
        String fileExtension = fileNameSplit[fileNameSplit.length - 1];

        if (!fileExtension.equals("jpeg") && !fileExtension.equals("jpg") && !fileExtension.equals("png"))
            throw new IllegalArgumentException("Wrong format");

        s3Client.putObject(new PutObjectRequest(bucketName, modifiedFilename, fileObj));
        fileObj.delete();


        File file = File.builder()
                .name(modifiedFilename)
                .path(String.format("%s/api/users/image/%s", serverAddress, modifiedFilename))
                .type(fileExtension)
                .task(null)
                .build();
        fileRepository.save(file);

        user.setImagePath(file.getPath());
        repository.save(user);
    }

    @Override
    public byte[] getImage(Principal connectedUser, String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to download the file", e);
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid authorization header!");
    }
}
