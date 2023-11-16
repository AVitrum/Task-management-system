package com.vitrum.api.services;

import java.security.Principal;
import java.util.Map;

public interface MemberService {

    void changeRole(Principal connectedUser, Map<String, String> request, String teamName);
    void kick(Principal connectedUser, Map<String, String> request, String teamName);
}
