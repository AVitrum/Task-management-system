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
public class BundleResponse {

    private String title;
    private String creatorEmail;
    private String performerEmail;
    private LocalDateTime assignmentTime;
    private LocalDateTime dueDate;
    private LocalDateTime changeTime;
    private List<TaskResponse> tasks;
}
