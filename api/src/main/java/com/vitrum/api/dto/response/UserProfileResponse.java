package com.vitrum.api.dto.response;

import com.vitrum.api.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private String id;
    private String email;
    private String username;
    private Role role;
}
