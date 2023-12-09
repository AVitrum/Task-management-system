package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/teams/{team}/bundles/{bundle}/tasks/{task}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping("/add")
    public ResponseEntity<?> add(
            @RequestBody Map<String, String> request,
            @PathVariable String team,
            @PathVariable String bundle,
            @PathVariable String task,
            Principal connectedUser
    ) {
        try {
            service.add(request, connectedUser, team, bundle, task);
            return ResponseEntity.status(HttpStatus.CREATED).body("Comment created successfully");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
