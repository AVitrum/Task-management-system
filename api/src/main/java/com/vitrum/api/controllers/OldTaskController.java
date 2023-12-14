package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.OldTaskService;
import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/{team}/{task}/history")
@RequiredArgsConstructor
public class OldTaskController {

    private final OldTaskService service;
    private final Converter converter;

    @GetMapping
    public ResponseEntity<?> findAllByTitle(
            @PathVariable String team,
            @PathVariable String task,
            Principal connectedUser
    ) {
        try {
            return ResponseEntity.ok(service.findAllByTitle(task, team, connectedUser));
        } catch (UsernameNotFoundException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{version}")
    public ResponseEntity<?> getByVersion(
            @PathVariable String team,
            @PathVariable String task,
            @PathVariable Long version,
            Principal connectedUser
    ) {
        try {
            return ResponseEntity.ok(converter.mapOldTaskToHistoryResponse(
                    service.getByVersion(task, team, version, connectedUser))
            );
        } catch (UsernameNotFoundException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable String team, @PathVariable String task, Principal connectedUser) {
        service.delete(task, team, connectedUser);
        return ResponseEntity.ok("Deleted");

    }
}