package com.vitrum.api.models;

import com.vitrum.api.models.enums.Status;
import com.vitrum.api.models.submodels.OldTask;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private LocalDateTime creationTime;
    private LocalDateTime dueDate;
    private Long priority;
    private Long version;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    @OneToMany(mappedBy = "task")
    private List<OldTask> oldTasks;

    @Override
    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String formattedCreationTime = creationTime.format(dateFormatter);
        String formattedDueDate = dueDate.format(dateFormatter);

        return "Task: " + title + '\n' +
                "description: " + description + '\n' +
                "creationTime: " + formattedCreationTime + '\n' +
                "dueDate: " + formattedDueDate + '\n' +
                "priority: " + priority + '\n' +
                "version: " + version + '\n' +
                "status: " + status.name() + '\n' +
                "creator: " + bundle.getCreator().getUser().getEmail() + '\n' +
                "performer: " + bundle.getPerformer().getUser().getEmail();
    }
}
