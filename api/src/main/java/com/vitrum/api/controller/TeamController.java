package com.vitrum.api.controller;

import com.vitrum.api.service.TeamService;
import com.vitrum.api.dto.Request.TeamCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(
        @RequestBody TeamCreationRequest request,
        Principal connectedUser
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, connectedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> showByName(
        @PathVariable String name
    ) {
        try {
            return ResponseEntity.ok(service.findByName(name));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> showAll() {
        return ResponseEntity.ok(service.getAll());
    }

}
