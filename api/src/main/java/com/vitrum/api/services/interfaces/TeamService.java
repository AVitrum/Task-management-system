package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.data.response.TeamCreationResponse;
import com.vitrum.api.data.response.TeamResponse;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface TeamService {

    void addToTeam(String teamName, Map<String, String> request);
    void changeStage(String team, Map<String, String> request, Principal connectedUser);
    TeamCreationResponse create(TeamCreationRequest request, Principal connectedUser);
    List<TeamResponse> getAll();
    List<TeamResponse> findByUser(Principal connectedUser);
    TeamResponse findByName(String name);
}
