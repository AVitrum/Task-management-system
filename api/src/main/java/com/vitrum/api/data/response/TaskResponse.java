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

    private Long id;
    private String title;
    private String description;
    private String status;
    private Boolean isCompleted;
    private MemberResponse creator;
    private MemberResponse performer;
    private List<String> categories;
    private LocalDateTime assignmentDate;
    private LocalDateTime changeTime;
    private List<CommentResponse> comments;
    private List<FileResponse> files;
}
