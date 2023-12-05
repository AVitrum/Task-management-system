package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.request.ChangeUserCredentials;
import com.vitrum.api.data.request.RegisterRequest;
import com.vitrum.api.data.response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface UserService {

    UserProfileResponse profile(HttpServletRequest request);
    void create(RegisterRequest request);
    void changeCredentials(ChangeUserCredentials request);
    void changeStatus(String username);

    void addImage(Principal connectedUser, MultipartFile file);
    byte[] getImage(Principal connectedUser, String fileName);
}
