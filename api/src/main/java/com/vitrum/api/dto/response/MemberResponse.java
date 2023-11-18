package com.vitrum.api.dto.response;

import com.vitrum.api.models.enums.RoleInTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private String id;
    private String name;
    private RoleInTeam role;
}
