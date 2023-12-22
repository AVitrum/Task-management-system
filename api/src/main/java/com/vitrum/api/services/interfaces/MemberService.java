package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.response.MemberResponse;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface MemberService {

    void addToTeam(Long teamId, Map<String, String> request);

    boolean isCurrentUserManager(Long teamId, Principal connectedUser);

    void changeRole(Long teamId, Principal connectedUser, Map<String, String> request);

    void kick(Long teamId, Principal connectedUser, Map<String, String> request);

    void changeEmailsMessagingStatus(Long teamId, Principal connectedUser);

    boolean getEmailsMessagingStatus(Long teamId, Principal connectedUser);

    List<MemberResponse> getAllByTeam(Long teamId, Principal connectedUser);
}
