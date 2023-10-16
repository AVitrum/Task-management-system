package com.vitrum.api.dto.Request;

import com.vitrum.api.validation.PasswordConstraint;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangePasswordRequest {

    private String currentPassword;
    @PasswordConstraint
    private String newPassword;
    private String confirmationPassword;
}
