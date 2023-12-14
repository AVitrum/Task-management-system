package com.vitrum.api.controllers;

import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.services.interfaces.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TeamCreationRequest request, Principal connectedUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, connectedUser));
    }

    @PutMapping("/{team}/changeStage")
    public ResponseEntity<?> changeStage(
            @PathVariable String team,
            @RequestBody Map<String, String> request,
            Principal connectedUser
    ) {
        service.changeStage(team, request, connectedUser);
        return ResponseEntity.ok("Successfully");
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> showByName(@PathVariable String name) {
        return ResponseEntity.ok(service.findByName(name));
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

