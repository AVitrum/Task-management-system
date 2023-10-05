package com.vitrum.api.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangePasswordRequest {

    private String currentPassword;
    private String newPassword;
    private String confirmationPassword;
}
