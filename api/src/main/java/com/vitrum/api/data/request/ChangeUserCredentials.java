package com.vitrum.api.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserCredentials {

    private String username;
    private String role;
    private String email;
    private String newUsername;
}
