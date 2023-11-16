package com.vitrum.api.controllers;

import com.vitrum.api.dto.Request.TaskRequest;
import com.vitrum.api.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/teams/{team}/bundles/{bundle}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestBody TaskRequest request,
            @PathVariable String team,
            @PathVariable String bundle,
            Principal connectedUser
    ) {
        try {
            service.create(request, connectedUser, team, bundle);
            return ResponseEntity.status(HttpStatus.CREATED).body("Task created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PatchMapping("/change/{task}")
    public ResponseEntity<?> change(
            @RequestBody TaskRequest request,
            @PathVariable String team,
            @PathVariable String task,
            @PathVariable String bundle,
            Principal connectedUser
    ) {
        try {
            service.change(request, task, connectedUser, team, bundle);
            return ResponseEntity.ok("Task changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{task}")
    public ResponseEntity<?> delete(
            @PathVariable String team,
            @PathVariable String task,
            @PathVariable String bundle,
            Principal connectedUser
    ) {
        try {
            service.delete(task, connectedUser, team, bundle);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
