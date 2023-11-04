package com.vitrum.api.manager.task.history;

import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams/{team}/tasks/history")
@RequiredArgsConstructor
public class OldTaskController {

    private final OldTaskService service;
    private final Converter converter;

    @GetMapping("/{creator}/{taskTitle}")
    public ResponseEntity<?> findAllByTitle(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String creator
    ) {
        try {
            return ResponseEntity.ok(service.findAllByTitle(taskTitle, creator, team));
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{creator}/{taskTitle}/{version}")
    public ResponseEntity<?> getByVersion(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String creator,
            @PathVariable Long version

    ) {
        try {
            return ResponseEntity.ok(converter.mapOldTaskToHistoryResponse(
                    service.getByVersion(taskTitle, creator, team, version))
            );
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{creator}/{taskTitle}/{version}")
    public ResponseEntity<?> restore(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String creator,
            @PathVariable Long version

    ) {
        try {
            service.restore(taskTitle, creator, team, version);
            return ResponseEntity.ok("Restored");
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{creator}/{taskTitle}")
    public ResponseEntity<?> delete(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String creator
    ) {
        try {
            service.delete(taskTitle, creator, team);
            return ResponseEntity.ok("Deleted");
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
