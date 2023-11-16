package com.vitrum.api.services;

import com.vitrum.api.dto.Request.ChangeUserCredentials;
import com.vitrum.api.dto.Request.RegisterRequest;
import com.vitrum.api.dto.Response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    UserProfileResponse profile(HttpServletRequest request);
    void create(RegisterRequest request);
    void changeCredentials(ChangeUserCredentials request);
    void ban(String username);
}
