package com.vitrum.api.team;

import com.vitrum.api.dto.Response.*;
import com.vitrum.api.team.RoleInTeam;
import com.vitrum.api.team.Team;
import com.vitrum.api.member.Member;
import com.vitrum.api.member.MemberRepository;
import com.vitrum.api.team.TeamRepository;
import com.vitrum.api.dto.Request.TeamCreationRequest;
import com.vitrum.api.user.User;
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
    private final MemberRepository memberRepository;

    public TeamCreationResponse create(TeamCreationRequest request, Principal connectedUser) {
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            var team = Team.builder()
                    .name(request.getName())
                    .members(new ArrayList<>())
                    .build();
            repository.save(team);
            var member = team.addUser(user, RoleInTeam.LEADER);
            memberRepository.save(member);
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

    public List<TeamResponse> getAll() {
        var teams = repository.findAll();
        return teams.stream().map(this::mapTeamToTeamResponse).collect(Collectors.toList());
    }

    public TeamResponse findByName(String name) {
        var team = repository.findByName(name).orElseThrow(() -> new IllegalArgumentException("Team not found"));
        return mapTeamToTeamResponse(team);
    }

    private TeamResponse mapTeamToTeamResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .members(getMemberResponse(team))
                .build();
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
