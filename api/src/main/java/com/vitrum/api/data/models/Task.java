package com.vitrum.api.data.models;

import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.TaskRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

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

    @Column(nullable = false, unique = true)
    private String title;
    private String description;
    private Long priority;
    private Long version;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    @OneToMany(mappedBy = "task")
    private List<OldTask> oldTasks;

    @OneToMany(mappedBy = "task")
    private List<File> files;

    public static Task findTaskByTitleAndBundle(TaskRepository repository, String taskTitle, Bundle bundle) {
        return repository.findByTitleAndBundle(taskTitle, bundle)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String formattedCreationTime = bundle.getAssignmentTime().format(dateFormatter);
        String formattedDueDate = bundle.getDueDate().format(dateFormatter);

        return "Task: " + title + '\n' +
                "description: " + description + '\n' +
                "assignmentTime: " + formattedCreationTime + '\n' +
                "dueDate: " + formattedDueDate + '\n' +
                "priority: " + priority + '\n' +
                "version: " + version + '\n' +
                "status: " + status.name() + '\n' +
                "creator: " + bundle.getCreator().getUser().getEmail() + '\n' +
                "performer: " + bundle.getPerformer().getUser().getEmail();
    }
}
