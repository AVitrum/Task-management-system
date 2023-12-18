package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.request.StageDueDatesRequest;
import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.data.response.TeamCreationResponse;
import com.vitrum.api.data.response.TeamResponse;

import java.security.Principal;
import java.util.List;

public interface TeamService {

    void setStageDueDates(StageDueDatesRequest request, String teamName, Principal connectedUser);
    void changeStage(String teamName);

    TeamResponse findByName(String name);

    TeamCreationResponse create(TeamCreationRequest request, Principal connectedUser);

    List<TeamResponse> getAll();
    List<TeamResponse> findByUser(Principal connectedUser);
}
