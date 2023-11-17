package com.vitrum.api.controllers;

import com.vitrum.api.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teams/{team}/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @PatchMapping("/changeRole")
    public ResponseEntity<?> changeRole(
            @RequestBody Map<String, String> request,
            @PathVariable String team
    ) {
        try {
            service.changeRole(request, team);
            return ResponseEntity.ok("Role changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @DeleteMapping("/kick")
    public ResponseEntity<?> kick(
            @RequestBody Map<String, String> request,
            @PathVariable String team
    ) {
        try {
            service.kick(request, team);
            return ResponseEntity.ok("Kicked successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}

