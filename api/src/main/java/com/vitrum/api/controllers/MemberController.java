package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        try {
            service.changeRole(connectedUser, request, team);
            return ResponseEntity.ok("Role changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/emailsMessagingStatus")
    public ResponseEntity<?> emailsMessagingStatus(
            @PathVariable String team,
            Principal connectedUser
    ) {
        try {
            return ResponseEntity.ok(service.getEmailsMessagingStatus(team, connectedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/changeEmailsMessagingStatus")
    public ResponseEntity<?> changeEmailsMessagingStatus(
            @PathVariable String team,
            Principal connectedUser
    ) {
        try {
            service.changeEmailsMessagingStatus(team, connectedUser);
            return ResponseEntity.ok("Changed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/kick")
    public ResponseEntity<?> kick(
            Principal connectedUser,
            @RequestBody Map<String, String> request,
            @PathVariable String team
    ) {
        try {
            service.kick(connectedUser, request, team);
            return ResponseEntity.ok("Kicked successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}

