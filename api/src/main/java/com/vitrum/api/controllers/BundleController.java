package com.vitrum.api.controllers;

import com.vitrum.api.data.request.BundleRequest;
import com.vitrum.api.services.interfaces.BundleService;
import com.vitrum.api.util.Converter;
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
    private final Converter converter;

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestBody BundleRequest request,
            @PathVariable String team,
            Principal connectedUser
    ) {
        try {
            service.create(team, connectedUser, request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Created");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @PathVariable String team,
            Principal connectedUser
    ) {
        try {
            return ResponseEntity.ok(service.findAll(team, connectedUser));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/{bundle}")
    public ResponseEntity<?> findByTitle(
            @PathVariable String team,
            @PathVariable String bundle,
            Principal connectedUser
    ) {
        try {
            return ResponseEntity.ok(converter.mapBundleToBundleResponse(
                    service.findByTitle(team, bundle, connectedUser))
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @DeleteMapping("/{bundle}")
    public ResponseEntity<?> deleteByTitle(
            @PathVariable String team,
            @PathVariable String bundle,
            Principal connectedUser
    ) {
        try {
            service.deleteByTitle(team, bundle, connectedUser);
            return ResponseEntity.ok("Deleted");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
