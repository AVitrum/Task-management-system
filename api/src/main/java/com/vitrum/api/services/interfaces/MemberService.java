package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.response.MemberResponse;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface MemberService {

    void addToTeam(String team, Map<String, String> request);

    boolean isCurrentUserManager(String teamName, Principal connectedUser);

    void changeRole(Principal connectedUser, Map<String, String> request, String teamName);

    void kick(Principal connectedUser, Map<String, String> request, String teamName);

    void changeEmailsMessagingStatus(String teamName, Principal connectedUser);

    boolean getEmailsMessagingStatus(String teamName, Principal connectedUser);

    List<MemberResponse> getAllByTeam(String team, Principal connectedUser);
}
