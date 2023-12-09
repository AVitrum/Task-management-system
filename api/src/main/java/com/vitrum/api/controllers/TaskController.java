package com.vitrum.api.controllers;

import com.vitrum.api.data.request.TaskRequest;
import com.vitrum.api.services.interfaces.TaskService;
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

    @PostMapping("/add")
    public ResponseEntity<?> add(
            @RequestBody TaskRequest request,
            @PathVariable String team,
            @PathVariable String bundle,
            Principal connectedUser
    ) {
        try {
            service.add(request, connectedUser, team, bundle);
            return ResponseEntity.status(HttpStatus.CREATED).body("Task created successfully");
        } catch (IllegalArgumentException | IllegalStateException e) {
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
            service.change(request, task, team, bundle, connectedUser);
            return ResponseEntity.ok("Task changed successfully");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/{task}")
    public ResponseEntity<?> getTask(
            @PathVariable String team,
            @PathVariable String bundle,
            @PathVariable String task,
            Principal connectedUser
    ) {
        try {
            return ResponseEntity.ok(service.getTask(task, connectedUser, team, bundle));
        } catch (IllegalArgumentException | IllegalStateException e) {
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
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
