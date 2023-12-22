package com.vitrum.api.controllers;

import com.vitrum.api.data.request.StageDueDatesRequest;
import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.services.interfaces.TeamService;
import com.vitrum.api.util.Converter;
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
    private final Converter converter;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TeamCreationRequest request, Principal connectedUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, connectedUser));
    }

    @PutMapping("/{team}/setStages")
    public ResponseEntity<?> setStage(
            @PathVariable Long team,
            @RequestBody StageDueDatesRequest request,
            Principal connectedUser
    ) {
        service.setStageDueDates(request, team, connectedUser);
        return ResponseEntity.ok("Successfully");
    }

    @GetMapping("/{team}")
    public ResponseEntity<?> findById(@PathVariable Long team) {
        return ResponseEntity.ok(converter.mapTeamToTeamResponse(service.findById(team)));
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

