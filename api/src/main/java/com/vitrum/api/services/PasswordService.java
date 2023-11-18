package com.vitrum.api.services;

import com.vitrum.api.dto.request.ChangePasswordRequest;
import com.vitrum.api.dto.request.ResetPasswordRequest;

import java.security.Principal;

public interface PasswordService {

    void changePassword(ChangePasswordRequest request, Principal connectedUser);
    void resetPassword(ResetPasswordRequest request);
    void getRecoverycode(String email);
}
