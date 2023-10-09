package com.vitrum.api.controller;

import com.vitrum.api.service.TeamService;
import com.vitrum.api.dto.Request.CreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/team/")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService service;

    @PostMapping("/create")
    public ResponseEntity<?> createTeam(
        @RequestBody CreationRequest request,
        @AuthenticationPrincipal UserDetails userDetails
        ) {
        try {
            return ResponseEntity.ok(service.createTeam(request, userDetails));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
