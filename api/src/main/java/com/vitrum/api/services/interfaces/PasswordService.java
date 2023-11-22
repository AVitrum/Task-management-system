package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.request.ChangePasswordRequest;
import com.vitrum.api.data.request.ResetPasswordRequest;

import java.security.Principal;

public interface PasswordService {

    void changePassword(ChangePasswordRequest request, Principal connectedUser);
    void resetPassword(ResetPasswordRequest request);
    void getRecoverycode(String email);
}
