package com.vitrum.api.manager.bundle;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/teams/{team}/bundles")
@RequiredArgsConstructor
public class BundleController {

    private final BundleService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(
           @RequestBody Map<String, String> request,
           @PathVariable String team,
           Principal connectedUser
    ) {
        try {
            service.create(team, connectedUser, request.get("title"));
            return ResponseEntity.status(HttpStatus.CREATED).body("Created");
        } catch (IllegalArgumentException | UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{bundle}/addPerformer")
    public ResponseEntity<?> addPerformer(
            @RequestBody Map<String, String> request,
            @PathVariable String team,
            @PathVariable String bundle,
            Principal connectedUser
    ) {
        try {
            service.addPerformer(team, bundle, connectedUser, request.get("performer"));
            return ResponseEntity.ok("Added");
        } catch (IllegalArgumentException | UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
