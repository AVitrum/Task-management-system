package com.vitrum.api.manager.task.main;

import com.vitrum.api.dto.Request.TaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/teams/{team}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestBody TaskRequest request,
            @PathVariable String team,
            Principal connectedUser
    ) {
        try {
            service.create(request, connectedUser, team);
            return ResponseEntity.status(HttpStatus.CREATED).body("Task created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PatchMapping("/changePerformer/{task}")
    public ResponseEntity<?> changePerformer(
            @RequestBody Map<String, String> request,
            @PathVariable String team,
            @PathVariable String task,
            Principal connectedUser
    ) {
        try {
            service.changePerformer(request, task, connectedUser, team);
            return ResponseEntity.ok("Performer changed successfully");
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
            Principal connectedUser
    ) {
        try {
            service.change(request, task, connectedUser, team);
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
            Principal connectedUser
    ) {
        try {
            service.delete(task, connectedUser, team);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
