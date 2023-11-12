package com.vitrum.api.manager.task.history;

import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams/{team}/bundles/{bundle}/tasks/history")
@RequiredArgsConstructor
public class OldTaskController {

    private final OldTaskService service;
    private final Converter converter;

    @GetMapping("/{creator}/{taskTitle}")
    public ResponseEntity<?> findAllByTitle(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String creator,
            @PathVariable String bundle
    ) {
        try {
            return ResponseEntity.ok(service.findAllByTitle(taskTitle, creator, team, bundle));
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{creator}/{taskTitle}/{version}")
    public ResponseEntity<?> getByVersion(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String creator,
            @PathVariable String bundle,
            @PathVariable Long version

    ) {
        try {
            return ResponseEntity.ok(converter.mapOldTaskToHistoryResponse(
                    service.getByVersion(taskTitle, creator, team, bundle, version))
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
            @PathVariable String bundle,
            @PathVariable Long version

    ) {
        try {
            service.restore(taskTitle, creator, team, bundle, version);
            return ResponseEntity.ok("Restored");
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{creator}/{taskTitle}")
    public ResponseEntity<?> delete(
            @PathVariable String team,
            @PathVariable String taskTitle,
            @PathVariable String creator,
            @PathVariable String bundle
    ) {
        try {
            service.delete(taskTitle, creator, team, bundle);
            return ResponseEntity.ok("Deleted");
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
