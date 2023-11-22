package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.data.response.TeamCreationResponse;
import com.vitrum.api.data.response.TeamResponse;

import java.util.List;

public interface TeamService {

    TeamCreationResponse create(TeamCreationRequest request);
    void addToTeam(String username, String teamName);
    List<TeamResponse> getAll();
    List<TeamResponse> findIfInTeam();
    TeamResponse findByName(String name);
}
