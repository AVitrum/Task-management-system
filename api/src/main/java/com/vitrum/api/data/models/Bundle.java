package com.vitrum.api.data.models;

import com.vitrum.api.data.enums.Status;
import com.vitrum.api.repositories.BundleRepository;
import com.vitrum.api.repositories.TaskRepository;
import com.vitrum.api.repositories.TeamRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bundle")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDateTime assignmentDate;
    private LocalDateTime dueDate;
    private LocalDateTime changeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Member creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performer_id")
    private Member performer;

    @OneToMany(mappedBy = "bundle")
    private List<Task> tasks;

    public static Bundle getBundleWithDateCheck(
            BundleRepository bundleRepository,
            TeamRepository teamRepository,
            TaskRepository taskRepository,
            String team,
            String bundleTitle
    ) {
        var bundle = Bundle.findBundle(bundleRepository, Team.findTeamByName(teamRepository, team), bundleTitle);
        var check = bundle.checkDate(taskRepository);
        if (check != null)
            throw new IllegalStateException(check);
        return bundle;
    }

    public static Bundle findBundle(BundleRepository bundleRepository, Team team, String title) {
        return bundleRepository.findByTeamAndTitle(team, title)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
    }

    public void saveChangeDate(BundleRepository repository) {
        this.setChangeTime(LocalDateTime.now());
        repository.save(this);
    }

    public String checkDate(TaskRepository taskRepository) {
        if (LocalDateTime.now().isAfter(this.getDueDate())) {
            this.getTasks().forEach(task -> {
                if (!task.getStatus().equals(Status.COMPLETED)
                        && !task.getStatus().equals(Status.DELETED)
                        && !task.getStatus().equals(Status.IN_REVIEW)
                        && !task.getStatus().equals(Status.DELAYED)
                ) {
                    task.setStatus(Status.OVERDUE);
                    taskRepository.save(task);
                }
            });
            return "Tasks that have not been marked as completed, approved, delayed, deleted, " +
                    "or are currently being reviewed are marked as overdue. " +
                    "Notify the manager of the need for an extension.";
        }
        return null;
    }

}

