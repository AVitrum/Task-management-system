package com.vitrum.api.manager.member;

import lombok.RequiredArgsConstructor;
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
            return ResponseEntity.ok("Changed");
        } catch (IllegalArgumentException | UsernameNotFoundException e) {
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
            return ResponseEntity.ok("Kicked");
        } catch (IllegalArgumentException | UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
