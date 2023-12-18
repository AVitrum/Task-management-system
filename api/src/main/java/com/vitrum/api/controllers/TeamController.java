package com.vitrum.api.controllers;

import com.vitrum.api.data.request.StageDueDatesRequest;
import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.services.interfaces.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TeamCreationRequest request, Principal connectedUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, connectedUser));
    }

    @PutMapping("/{team}/setStages")
    public ResponseEntity<?> setStage(
            @PathVariable String team,
            @RequestBody StageDueDatesRequest request,
            Principal connectedUser
    ) {
        service.setStageDueDates(request, team, connectedUser);
        return ResponseEntity.ok("Successfully");
    }

    @GetMapping("/{team}")
    public ResponseEntity<?> showByName(@PathVariable String team) {
        return ResponseEntity.ok(service.findByName(team));
    }

    @GetMapping("/findByUser")
    public ResponseEntity<?> showIfInTeam(Principal connectedUser) {
        return ResponseEntity.ok(service.findByUser(connectedUser));
    }

    @GetMapping
    public ResponseEntity<?> showAll() {
        return ResponseEntity.ok(service.getAll());
    }
}

