package com.vitrum.api.service;

import com.vitrum.api.dto.Response.UserProfileResponse;
import com.vitrum.api.entity.RoleInTeam;
import com.vitrum.api.entity.Team;
import com.vitrum.api.entity.TeamMembership;
import com.vitrum.api.repository.TeamMembershipRepository;
import com.vitrum.api.repository.TeamRepository;
import com.vitrum.api.dto.Request.TeamCreationRequest;
import com.vitrum.api.dto.Response.TeamCreationResponse;
import com.vitrum.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository repository;
    private final TeamMembershipRepository teamMembershipRepository;

    public TeamCreationResponse createTeam(TeamCreationRequest request, Principal connectedUser) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            var team = Team.builder()
                    .name(request.getName())
                    .members(new ArrayList<>())
                    .build();
            repository.save(team);
            addMember(user, RoleInTeam.MANAGER, team);
            return TeamCreationResponse.builder()
                    .id(team.getId())
                    .name(team.getName())
                    .creator(mapUserToUserProfileResponse(user))
                    .build();
        }
        catch (IllegalArgumentException e) {
            throw new IllegalStateException("Can't create");
        }
    }

    private UserProfileResponse mapUserToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getTrueUsername())
                .role(user.getRole())
                .build();
    }

    private void addMember(User user, RoleInTeam role, Team team) {
        var member = TeamMembership.builder()
                .user(user)
                .role(role)
                .team(team)
                .build();
        team.getMembers().add(member);
        teamMembershipRepository.save(member);
    }
}
