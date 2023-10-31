package com.vitrum.api.manager.task;

import com.vitrum.api.dto.Request.TaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/teams/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping("/{team}/create")
    public ResponseEntity<?> create(
            @RequestBody TaskRequest request,
            @PathVariable String team,
            Principal connectedUser
    ) {
        try {
            service.create(request, connectedUser, team);
            return ResponseEntity.status(HttpStatus.CREATED).body("Created");
        } catch (IllegalArgumentException | UsernameNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{team}/change/{taskTitle}")
    public ResponseEntity<?> change(
            @RequestBody TaskRequest request,
            @PathVariable String team,
            @PathVariable String taskTitle,
            Principal connectedUser
    ) {
        try {
            service.change(request, taskTitle, connectedUser, team);
            return ResponseEntity.ok("Changed");
        } catch (IllegalArgumentException | UsernameNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


}
