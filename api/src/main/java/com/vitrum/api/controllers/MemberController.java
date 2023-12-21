package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/{team}/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @PostMapping("/addMember")
    public ResponseEntity<?> addMember(@PathVariable Long team, @RequestBody Map<String, String> request) {
        service.addToTeam(team, request);
        return ResponseEntity.ok("Member added successfully");
    }

    @GetMapping("/checkPermission")
    public ResponseEntity<Boolean> isCurrentUserManager(@PathVariable Long team, Principal connectedUser) {
        return ResponseEntity.ok(service.isCurrentUserManager(team, connectedUser));
    }

    @PatchMapping("/changeRole")
    public ResponseEntity<?> changeRole(
            Principal connectedUser,
            @RequestBody Map<String, String> request,
            @PathVariable Long team
    ) {
        service.changeRole(team, connectedUser, request);
        return ResponseEntity.ok("Role changed successfully");
    }

    @PatchMapping("/changeEmailsMessagingStatus")
    public ResponseEntity<?> changeEmailsMessagingStatus(@PathVariable Long team, Principal connectedUser) {
        service.changeEmailsMessagingStatus(team, connectedUser);
        return ResponseEntity.ok("Changed");
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllByTeam(@PathVariable Long team, Principal connectedUser) {
        return ResponseEntity.ok(service.getAllByTeam(team, connectedUser));
    }

    @GetMapping("/emailsMessagingStatus")
    public ResponseEntity<?> emailsMessagingStatus(@PathVariable Long team, Principal connectedUser) {
        return ResponseEntity.ok(service.getEmailsMessagingStatus(team, connectedUser));
    }

    @DeleteMapping("/kick")
    public ResponseEntity<?> kick(
            Principal connectedUser,
            @RequestBody Map<String, String> request,
            @PathVariable Long team
    ) {
        service.kick(team, connectedUser, request);
        return ResponseEntity.ok("Kicked successfully");
    }
}