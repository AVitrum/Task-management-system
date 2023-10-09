package com.vitrum.api.dto.Response;

import com.vitrum.api.entity.TeamMembership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResponse {

    private String name;
    private Long id;
    private String role;
}
