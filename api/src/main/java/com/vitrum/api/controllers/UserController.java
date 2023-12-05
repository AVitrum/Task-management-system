package com.vitrum.api.controllers;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.vitrum.api.services.interfaces.UserService;
import com.vitrum.api.data.request.ChangeUserCredentials;
import com.vitrum.api.data.request.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody RegisterRequest request) {
        try {
            service.create(request);
            return ResponseEntity.ok("Created");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/image")
    public ResponseEntity<?> addImage(
            @RequestParam(value = "file") MultipartFile file,
            Principal connectedUser
    ) {
        try {
            service.addImage(connectedUser, file);
            return ResponseEntity.ok("Added");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/image/{file}")
    public ResponseEntity<?> getImage(
            @PathVariable String file,
            Principal connectedUser
    ) {
        try {
            byte[] data = service.getImage(connectedUser, file);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(data);
        } catch (AmazonS3Exception | IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("File not found");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.profile(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/changeCredentials")
    public ResponseEntity<?> changeCredentials(@RequestBody ChangeUserCredentials request) {
        try {
            service.changeCredentials(request);
            return ResponseEntity.ok("Changed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/ban")
    public ResponseEntity<?> ban(@RequestBody Map<String, String> username) {
        try {
            service.changeStatus(username.get("username"));
            return ResponseEntity.ok("Okay");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
