package com.vitrum.api.services;

import com.vitrum.api.dto.Request.AuthenticationRequest;
import com.vitrum.api.dto.Request.RegisterRequest;
import com.vitrum.api.dto.Response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {

    void register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
