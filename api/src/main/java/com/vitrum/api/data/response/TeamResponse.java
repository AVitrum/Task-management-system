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
public class TeamResponse {

    private Long id;
    private String name;
    private List<MemberResponse> members;
    private String stage;
    private LocalDateTime stageDueDate;
}
