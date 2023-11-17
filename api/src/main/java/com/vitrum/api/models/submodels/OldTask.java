package com.vitrum.api.models.submodels;

import com.vitrum.api.models.Task;
import com.vitrum.api.models.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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
    private Task task;
}
