package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/teams/{team}/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @PatchMapping("/changeRole")
    public ResponseEntity<?> changeRole(
            Principal connectedUser,
            @RequestBody Map<String, String> request,
            @PathVariable String team
    ) {
        service.changeRole(connectedUser, request, team);
        return ResponseEntity.ok("Role changed successfully");
    }

    @GetMapping("/emailsMessagingStatus")
    public ResponseEntity<?> emailsMessagingStatus(@PathVariable String team, Principal connectedUser) {
        return ResponseEntity.ok(service.getEmailsMessagingStatus(team, connectedUser));
    }

    @PatchMapping("/changeEmailsMessagingStatus")
    public ResponseEntity<?> changeEmailsMessagingStatus(@PathVariable String team, Principal connectedUser) {
        service.changeEmailsMessagingStatus(team, connectedUser);
        return ResponseEntity.ok("Changed");
    }

    @DeleteMapping("/kick")
    public ResponseEntity<?> kick(
            Principal connectedUser,
            @RequestBody Map<String, String> request,
            @PathVariable String team
    ) {
        service.kick(connectedUser, request, team);
        return ResponseEntity.ok("Kicked successfully");
    }
}