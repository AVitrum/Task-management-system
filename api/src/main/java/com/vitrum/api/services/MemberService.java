package com.vitrum.api.services;

import java.util.Map;

public interface MemberService {

    void changeRole(Map<String, String> request, String teamName);
    void kick(Map<String, String> request, String teamName);
    void changeEmailsMessagingStatus(String teamName);
    boolean getEmailsMessagingStatus(String teamName);
}
