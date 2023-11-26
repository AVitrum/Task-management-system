package com.vitrum.api.services.implementation;

import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.data.response.TeamCreationResponse;
import com.vitrum.api.data.response.TeamResponse;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.models.User;
import com.vitrum.api.data.enums.RoleInTeam;
import com.vitrum.api.repositories.MemberRepository;
import com.vitrum.api.repositories.TeamRepository;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.services.interfaces.TeamService;
import com.vitrum.api.util.Converter;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository repository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final Converter converter;
    private final MessageUtil messageUtil;

    @Override
    public TeamCreationResponse create(TeamCreationRequest request) {
        try {
            var user = User.getAuthUser(userRepository);

            if (repository.existsByName(request.getName()))
                throw new IllegalArgumentException("Team with the same name already exists");

            var team = Team.builder()
                    .name(request.getName())
                    .members(new ArrayList<>())
                    .build();
            repository.save(team);

            var member = Member.builder()
                    .user(user)
                    .role(RoleInTeam.LEADER)
                    .isEmailsAllowed(true)
                    .team(team)
                    .build();
            memberRepository.save(member);

            return TeamCreationResponse.builder()
                    .id(team.getId())
                    .name(team.getName())
                    .creator(converter.mapUserToUserProfileResponse(user))
                    .build();

        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Can't create");
        }
    }

    @Override
    public void addToTeam(String teamName, Map<String, String> request) {
        var team = repository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Can't find team by this name"));
        var user = userRepository.findByUsername(request.get("username"))
                .orElseThrow(() -> new UsernameNotFoundException("Can't find user"));

        if (memberRepository.existsByUserAndTeam(user, team))
            throw new IllegalArgumentException("The user is already in the team");

        var member = Member.builder()
                .user(user)
                .role(RoleInTeam.MEMBER)
                .team(team)
                .build();
        memberRepository.save(member);

        messageUtil.sendMessage(
                member,
                String.format("You have been added to the team: %s.", teamName),
                "TMS Info"
        );
    }

    public List<TeamResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(converter::mapTeamToTeamResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamResponse> findIfInTeam() {
        var user = User.getAuthUser(userRepository);
        List<Member> members = memberRepository.findAllByUser(user);

        return members.stream()
                .map(member -> converter.mapTeamToTeamResponse(member.getTeam()))
                .collect(Collectors.toList());
    }

    @Override
    public TeamResponse findByName(String name) {
        var team = repository.findByName(name).orElseThrow(() -> new IllegalArgumentException("Team not found"));
        return converter.mapTeamToTeamResponse(team);
    }

}
