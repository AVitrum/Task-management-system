package com.vitrum.api.services;

import com.vitrum.api.dto.Request.ChangePasswordRequest;
import com.vitrum.api.dto.Request.ResetPasswordRequest;

import java.security.Principal;

public interface PasswordService {

    void changePassword(ChangePasswordRequest request, Principal connectedUser);
    void resetPassword(ResetPasswordRequest request);
    void getRecoverycode(String email);
}
