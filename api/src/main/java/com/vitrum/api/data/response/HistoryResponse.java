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
    private String status;
    private LocalDateTime changeTime;
    private String message;
    private String user;
}
