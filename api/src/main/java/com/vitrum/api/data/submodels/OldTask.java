package com.vitrum.api.data.submodels;

import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "old_tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OldTask {

    @Id
    private String id;

    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime dueDate;
    private LocalDateTime changeTime;
    private Long priority;
    private Long version;

    private Status status;

    @DBRef
    private List<Comment> comments;

    @DBRef
    private Task task;

    public static OldTask findByTaskAndVersion(Task task, Long version) {
        for (var oldTask : task.getOldTasks())
            if (oldTask != null && oldTask.getVersion().equals(version))
                return oldTask;
        throw new IllegalArgumentException("Version not found");
    }
}
