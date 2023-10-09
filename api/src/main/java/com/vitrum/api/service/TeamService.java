package com.vitrum.api.service;

import com.vitrum.api.dto.Response.UserProfileResponse;
import com.vitrum.api.entity.RoleInTeam;
import com.vitrum.api.entity.Team;
import com.vitrum.api.repository.TeamRepository;
import com.vitrum.api.dto.Request.CreationRequest;
import com.vitrum.api.dto.Response.CreationResponse;
import com.vitrum.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository repository;

    public CreationResponse createTeam(CreationRequest request, UserDetails userDetails) {
        try {
            var user = (User) userDetails;
            var team = Team.builder()
                    .name(request.getName())
                    .members(new ArrayList<>())
                    .build();
            team.addMember(user, RoleInTeam.MANAGER);
            repository.save(team);
            return CreationResponse.builder()
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
}
