package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.OldTaskService;
import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/teams/{team}/bundles/{bundle}/tasks/history")
@RequiredArgsConstructor
public class OldTaskController {

    private final OldTaskService service;
    private final Converter converter;

    @GetMapping("/{taskTitle}")
    public ResponseEntity<?> findAllByTitle(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String bundle,
            Principal connectedUser
    ) {
        try {
            return ResponseEntity.ok(service.findAllByTitle(taskTitle, team, bundle, connectedUser));
        } catch (UsernameNotFoundException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{taskTitle}/{version}")
    public ResponseEntity<?> getByVersion(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String bundle,
            @PathVariable Long version,
            Principal connectedUser
    ) {
        try {
            return ResponseEntity.ok(converter.mapOldTaskToHistoryResponse(
                    service.getByVersion(taskTitle, team, bundle, version, connectedUser))
            );
        } catch (UsernameNotFoundException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{taskTitle}/{version}")
    public ResponseEntity<?> restore(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String bundle,
            @PathVariable Long version,
            Principal connectedUser

    ) {
        try {
            service.restore(taskTitle, team, bundle, version, connectedUser);
            return ResponseEntity.ok("Restored");
        } catch (UsernameNotFoundException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{taskTitle}")
    public ResponseEntity<?> delete(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String bundle,
            Principal connectedUser
    ) {
        try {
            service.delete(taskTitle, team, bundle, connectedUser);
            return ResponseEntity.ok("Deleted");
        } catch (UsernameNotFoundException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
