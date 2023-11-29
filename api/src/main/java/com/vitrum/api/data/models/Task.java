package com.vitrum.api.data.models;

import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.submodels.Comment;
import com.vitrum.api.data.submodels.OldTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Document(collection = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    private String id;

    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime dueDate;
    private Long priority;
    private Long version;

    private Status status;

    @DBRef
    private Bundle bundle;

    @DBRef
    private List<OldTask> oldTasks;

    @DBRef
    private List<Comment> comments;

    public static Task findTaskByTitleAndBundle(Bundle bundle, String title) {
        for (var task : bundle.getTasks())
            if (task != null && task.getTitle().equals(title))
                return task;
        throw new IllegalArgumentException("Task not found");
    }

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
