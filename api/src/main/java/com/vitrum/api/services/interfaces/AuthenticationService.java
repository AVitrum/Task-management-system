package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.request.AuthenticationRequest;
import com.vitrum.api.data.request.RegisterRequest;
import com.vitrum.api.data.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {

    void register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
