package com.vitrum.api.controller;

import com.vitrum.api.service.TeamService;
import com.vitrum.api.dto.Request.TeamCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/team/")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService service;

    @PostMapping("/create")
    public ResponseEntity<?> createTeam(
        @RequestBody TeamCreationRequest request,
        Principal connectedUser
    ) {
        try {
            return ResponseEntity.ok(service.createTeam(request, connectedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{team}")
    public ResponseEntity<?> addUserToTeam(
            @PathVariable String team,
            @RequestBody Map<String, String> requestBody
    ) {
        String username = requestBody.get("username");
        try {
            return ResponseEntity.ok(service.addUserToTeam(username, team));
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> showTeamByName(
        @PathVariable String name
    ) {
        try {
            return ResponseEntity.ok(service.findByName(name));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
