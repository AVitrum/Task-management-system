package com.vitrum.api.services.interfaces;

import java.security.Principal;
import java.util.Map;

public interface CommentService {

    void add(Map<String, String> request, Principal connectedUser, Long teamId, Long taskId);
}
