package com.vitrum.api.data.request;

import com.vitrum.api.data.enums.RegistrationSource;
import com.vitrum.api.validation.PasswordConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String username;
    private String email;
    @PasswordConstraint
    private String password;
    private String role;
    private RegistrationSource source;
    private String imagePath;
}
