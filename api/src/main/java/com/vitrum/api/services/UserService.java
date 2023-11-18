package com.vitrum.api.services;

import com.vitrum.api.dto.request.ChangeUserCredentials;
import com.vitrum.api.dto.request.RegisterRequest;
import com.vitrum.api.dto.response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    UserProfileResponse profile(HttpServletRequest request);
    void create(RegisterRequest request);
    void changeCredentials(ChangeUserCredentials request);
    void ban(String username);
}
