package com.vitrum.api.services;

import com.vitrum.api.dto.request.TeamCreationRequest;
import com.vitrum.api.dto.response.TeamCreationResponse;
import com.vitrum.api.dto.response.TeamResponse;

import java.util.List;

public interface TeamService {

    TeamCreationResponse create(TeamCreationRequest request);
    void addToTeam(String username, String teamName);
    List<TeamResponse> getAll();
    List<TeamResponse> findIfInTeam();
    TeamResponse findByName(String name);
}
