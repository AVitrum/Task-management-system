package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/{team}/{task}")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping("/addComment")
    public ResponseEntity<?> add(
            @RequestBody Map<String, String> request,
            @PathVariable String team,
            @PathVariable String task,
            Principal connectedUser
    ) {
        service.add(request, connectedUser, team, task);
        return ResponseEntity.status(HttpStatus.CREATED).body("Comment created successfully");
    }
}
