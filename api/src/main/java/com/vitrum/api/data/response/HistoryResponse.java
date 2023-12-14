package com.vitrum.api.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponse {
    private Long taskId;
    private Long id;
    private Long version;
    private String title;
    private String description;
    private String status;
    private List<CommentResponse> comments;
}
