package com.vitrum.api.controllers;

import com.vitrum.api.data.request.TaskRequest;
import com.vitrum.api.services.interfaces.TaskService;
import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/{team}")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;
    private final Converter converter;

    @PostMapping("/createTask")
    public ResponseEntity<?> create(
            @RequestBody TaskRequest request,
            @PathVariable String team,
            Principal connectedUser
    ) {
        service.create(team, connectedUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created");
    }

    @PutMapping("/{task}/update")
    public ResponseEntity<?> update(
            @RequestBody TaskRequest request,
            @PathVariable String team,
            @PathVariable String task,
            Principal connectedUser
    ) {
        service.update(team, task, connectedUser, request);
        return ResponseEntity.status(HttpStatus.OK).body("Updated");
    }

    @PatchMapping("/{task}/addPerformer")
    public ResponseEntity<?> addPerformer(
            @RequestBody Map<String, String> request,
            @PathVariable String team,
            @PathVariable String task,
            Principal connectedUser
    ) {
        service.addPerformer(team, task, connectedUser, request.get("performer"));
        return ResponseEntity.ok("Added");
    }

    @PutMapping("/{task}/restore")
    public ResponseEntity<?> restore(@PathVariable String team, @PathVariable String task, Principal connectedUser) {
        service.restoreByTitle(task, team, connectedUser);
        return ResponseEntity.ok("Restored");
    }

    @PatchMapping("/{task}/changeCategory")
    public ResponseEntity<?> changeCategory (
            @RequestBody Map<String, String> request,
            @PathVariable String team,
            @PathVariable String task,
            Principal connectedUser
    ) {
        return ResponseEntity.ok(service.changeCategory(request, team, task, connectedUser));
    }

    @PatchMapping("/{task}/confirmTask")
    public ResponseEntity<?> confirmTask(
            @PathVariable String team,
            @PathVariable String task,
            Principal connectedUser
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(service.confirmTask(team, task, connectedUser));
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> findAll(@PathVariable String team, Principal connectedUser) {
        return ResponseEntity.ok(service.findAll(team, connectedUser));
    }

    @GetMapping("/tasks/inReview")
    public ResponseEntity<?> findAllInReview(@PathVariable String team, Principal connectedUser) {
        return ResponseEntity.ok(service.findAllInReview(team, connectedUser));
    }

    @GetMapping("/{task}")
    public ResponseEntity<?> findByTitle(
            @PathVariable String team,
            @PathVariable String task,
            Principal connectedUser
    ) {
        return ResponseEntity.ok(converter.mapTaskToTaskResponse(service.findByTitle(team, task, connectedUser)));
    }

    @DeleteMapping("/{task}")
    public ResponseEntity<?> deleteByTitle(
            @PathVariable String team,
            @PathVariable String task,
            Principal connectedUser
    ) {
        service.deleteByTitle(team, task, connectedUser);
        return ResponseEntity.ok("Deleted");
    }
}