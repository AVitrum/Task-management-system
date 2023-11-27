package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teams/{team}/bundles/{bundle}/tasks/{task}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestBody Map<String, String> request,
            @PathVariable String team,
            @PathVariable String bundle,
            @PathVariable String task
    ) {
        try {
            service.create(team, bundle, task, request);
            return ResponseEntity.ok("Created");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
