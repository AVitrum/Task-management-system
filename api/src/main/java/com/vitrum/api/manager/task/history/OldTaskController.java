package com.vitrum.api.manager.task.history;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams/{team}/tasks/history")
@RequiredArgsConstructor
public class OldTaskController {

    private final OldTaskService service;

    @GetMapping("/{creator}/{taskTitle}")
    public ResponseEntity<?> findAllByTitle(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String creator

    ) {
        try {
            return ResponseEntity.ok(service.findAllByTitle(taskTitle, creator, team));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{creator}/{taskTitle}")
    public ResponseEntity<?> delete(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String creator
    ) {
        try {
            service.delete(taskTitle, creator, team);
            return ResponseEntity.ok("Deleted");
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
