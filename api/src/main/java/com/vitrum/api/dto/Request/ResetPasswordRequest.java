package com.vitrum.api.dto.Request;

import com.vitrum.api.validation.PasswordConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    private String email;
    private Long code;
    @PasswordConstraint
    private String newPassword;
    private String confirmationPassword;
}
