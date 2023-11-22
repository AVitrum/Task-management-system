package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.BundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teams/{team}/bundles")
@RequiredArgsConstructor
public class BundleController {

    private final BundleService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(
           @RequestBody Map<String, String> request,
           @PathVariable String team
    ) {
        try {
            service.create(team, request.get("title"));
            return ResponseEntity.status(HttpStatus.CREATED).body("Created");
        } catch (IllegalArgumentException | UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{bundle}/addPerformer")
    public ResponseEntity<?> addPerformer(
            @RequestBody Map<String, String> request,
            @PathVariable String team,
            @PathVariable String bundle
    ) {
        try {
            service.addPerformer(team, bundle, request.get("performer"));
            return ResponseEntity.ok("Added");
        } catch (IllegalArgumentException | UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{bundle}/findByUser")
    public ResponseEntity<?> findByUser(
            @PathVariable String team,
            @PathVariable String bundle
    ) {
        try {
            return ResponseEntity.ok(service.findByUser(team, bundle));
        } catch (IllegalArgumentException | UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
