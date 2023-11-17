package com.vitrum.api.controllers;

import com.vitrum.api.dto.Request.TaskRequest;
import com.vitrum.api.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams/{team}/bundles/{bundle}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestBody TaskRequest request,
            @PathVariable String team,
            @PathVariable String bundle
    ) {
        try {
            service.create(request, team, bundle);
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
            @PathVariable String bundle
    ) {
        try {
            service.change(request, task, team, bundle);
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
            @PathVariable String bundle
    ) {
        try {
            service.delete(task, team, bundle);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
