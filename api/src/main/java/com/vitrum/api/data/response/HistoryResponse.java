package com.vitrum.api.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime creationTime;
    private LocalDateTime dueDate;
    private LocalDateTime changeTime;
    private Long priority;
    private String status;
    private MemberResponse creator;
}
