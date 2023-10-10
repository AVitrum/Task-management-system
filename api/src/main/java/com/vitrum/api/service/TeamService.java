package com.vitrum.api.service;

import com.vitrum.api.dto.Response.*;
import com.vitrum.api.entity.enums.RoleInTeam;
import com.vitrum.api.entity.Team;
import com.vitrum.api.entity.Member;
import com.vitrum.api.repository.TeamMembershipRepository;
import com.vitrum.api.repository.TeamRepository;
import com.vitrum.api.dto.Request.TeamCreationRequest;
import com.vitrum.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public TeamResponse findByName(String name) {
        var team = repository.findByName(name).orElseThrow(() -> new IllegalArgumentException("Team not found"));
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .members(getMemberResponse(team))
                .build();
    }

    private void addMember(User user, RoleInTeam role, Team team) {
        var member = Member.builder()
                .user(user)
                .role(role)
                .team(team)
                .build();
        team.getMembers().add(member);
        teamMembershipRepository.save(member);
    }

    private UserProfileResponse mapUserToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getTrueUsername())
                .role(user.getRole())
                .build();
    }

    private List<MemberResponse> getMemberResponse(Team team) {
        List<Member> members = team.getMembers();
        return members.stream()
                .map(this::mapMemberToMemberResponse)
                .collect(Collectors.toList());
    }

    private MemberResponse mapMemberToMemberResponse(Member membership) {
        return MemberResponse.builder()
                .id(membership.getId())
                .name(membership.getUser().getTrueUsername())
                .role(membership.getRole())
                .build();
    }
}
