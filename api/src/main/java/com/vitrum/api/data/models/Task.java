package com.vitrum.api.data.models;

import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.enums.TaskCategory;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.CommentRepository;
import com.vitrum.api.repositories.OldTaskRepository;
import com.vitrum.api.repositories.TaskRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "task")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Long version;
    private Boolean completed;
    private LocalDateTime assignmentDate;
    private LocalDateTime changeTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ElementCollection(targetClass = TaskCategory.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "task_categories", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "category")
    private Set<TaskCategory> categories = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Member creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performer_id")
    private Member performer;

    @OneToMany(mappedBy = "task")
    private List<OldTask> oldTasks;

    @OneToMany(mappedBy = "task")
    private List<Comment> comments;

    public static Task findTask(TaskRepository taskRepository, Team team, Long id) {
        return taskRepository.findByIdAndTeam(id, team)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    public void saveWithChangeDate(TaskRepository repository) {
        this.setChangeTime(LocalDateTime.now());
        repository.save(this);
    }

    public void delete(
            TaskRepository taskRepository,
            CommentRepository commentRepository,
            OldTaskRepository oldTaskRepository
    ) {
        List<OldTask> oldTasks = this.getOldTasks();
        oldTaskRepository.deleteAll(oldTasks);

        commentRepository.deleteAll(this.getComments());
        taskRepository.delete(this);
    }
}

