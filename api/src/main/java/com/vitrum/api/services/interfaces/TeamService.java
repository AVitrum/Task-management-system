package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.data.response.TeamCreationResponse;
import com.vitrum.api.data.response.TeamResponse;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface TeamService {

    TeamCreationResponse create(TeamCreationRequest request, Principal connectedUser);
    void addToTeam(String teamName, Map<String, String> request);
    List<TeamResponse> getAll();
    List<TeamResponse> findIfInTeam(Principal connectedUser);
    TeamResponse findByName(String name);
}
