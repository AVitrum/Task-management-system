package com.vitrum.api.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> files;
}
