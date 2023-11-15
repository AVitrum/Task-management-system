package com.vitrum.api.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime dueDate;
    private Long priority;
    private Long version;
    private String status;
}
