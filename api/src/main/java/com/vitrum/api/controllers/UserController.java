package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.UserService;
import com.vitrum.api.data.request.ChangeUserCredentials;
import com.vitrum.api.data.request.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        service.create(request);
        return ResponseEntity.ok("Created");
    }

    @PatchMapping("/image")
    public ResponseEntity<?> addImage(@RequestParam(value = "file") MultipartFile file, Principal connectedUser) {
        service.addImage(connectedUser, file);
        return ResponseEntity.ok("Added");
    }

    @GetMapping("/image/{file}")
    public ResponseEntity<?> getImage(@PathVariable String file, Principal connectedUser) {
        byte[] data = service.getImage(connectedUser, file);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(data);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpServletRequest request) {
        return ResponseEntity.ok(service.profile(request));
    }

    @PatchMapping("/changeCredentials")
    public ResponseEntity<?> changeCredentials(@RequestBody ChangeUserCredentials request) {
        service.changeCredentials(request);
        return ResponseEntity.ok("Changed");
    }

    @PutMapping("/ban")
    public ResponseEntity<?> ban(@RequestBody Map<String, String> username) {
        service.changeStatus(username.get("username"));
        return ResponseEntity.ok("Okay");
    }
}
