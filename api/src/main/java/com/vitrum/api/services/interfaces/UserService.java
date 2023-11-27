package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.request.ChangeUserCredentials;
import com.vitrum.api.data.request.RegisterRequest;
import com.vitrum.api.data.response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    UserProfileResponse profile(HttpServletRequest request);
    void create(RegisterRequest request);
    void changeCredentials(ChangeUserCredentials request);
    void changeStatus(String username);
}
