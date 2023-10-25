package com.vitrum.api.dto.Response;

import com.vitrum.api.credentials.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String email;
    private String username;
    private Role role;
}
