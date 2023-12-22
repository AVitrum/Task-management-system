package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.request.StageDueDatesRequest;
import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.data.response.TeamCreationResponse;
import com.vitrum.api.data.response.TeamResponse;

import java.security.Principal;
import java.util.List;

public interface TeamService {

    void setStageDueDates(StageDueDatesRequest request, Long teamId, Principal connectedUser);
    void changeStage(Long teamId);

    Team findById(Long id);

    TeamCreationResponse create(TeamCreationRequest request, Principal connectedUser);

    List<TeamResponse> getAll();
    List<TeamResponse> findByUser(Principal connectedUser);
}
