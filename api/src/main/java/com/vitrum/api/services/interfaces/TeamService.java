package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.data.response.TeamCreationResponse;
import com.vitrum.api.data.response.TeamResponse;

import java.util.List;
import java.util.Map;

public interface TeamService {

    TeamCreationResponse create(TeamCreationRequest request);
    void addToTeam(String teamName, Map<String, String> request);
    List<TeamResponse> getAll();
    List<TeamResponse> findIfInTeam();
    TeamResponse findByName(String name);
}
