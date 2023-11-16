package com.vitrum.api.services;

import com.vitrum.api.dto.Request.TeamCreationRequest;
import com.vitrum.api.dto.Response.TeamCreationResponse;
import com.vitrum.api.dto.Response.TeamResponse;

import java.security.Principal;
import java.util.List;

public interface TeamService {

    TeamCreationResponse create(TeamCreationRequest request, Principal connectedUser);
    void addToTeam(String username, String teamName);
    List<TeamResponse> getAll();
    List<TeamResponse> findIfInTeam(Principal connectedUser);
    TeamResponse findByName(String name);
}
