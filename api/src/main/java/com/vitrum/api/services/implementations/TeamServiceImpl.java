package com.vitrum.api.services.implementations;

import com.vitrum.api.data.enums.StageType;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.submodels.TeamStage;
import com.vitrum.api.repositories.TeamRepository;
import com.vitrum.api.repositories.TeamStageRepository;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.data.response.*;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.repositories.MemberRepository;
import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.data.models.User;
import com.vitrum.api.data.enums.RoleInTeam;
import com.vitrum.api.services.interfaces.TeamService;
import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
    private final TeamStageRepository teamStageRepository;
    private final Converter converter;

    @Override
    public TeamCreationResponse create(TeamCreationRequest request, Principal connectedUser) {
        try {
            var user = User.getUserFromPrincipal(connectedUser);
            var team = Team.builder()
                    .name(request.getName().replaceAll("\\s", "_"))
                    .members(new ArrayList<>())
                    .build();
            repository.save(team);

            TeamStage.create(teamStageRepository, team, StageType.PREPARATION, null);
            createMember(user, team, "Leader");

            return TeamCreationResponse.builder()
                    .id(team.getId())
                    .name(team.getName())
                    .creator(converter.mapUserToUserProfileResponse(user))
                    .build();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Can't create");
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Team with the same name already exists");
        }
    }

    @Override
    public void addToTeam(String teamName, Map<String, String> request) {
        var team = Team.findTeamByName(repository, teamName);
        var user = userRepository.findByUsername(request.get("username"))
                .orElseThrow(() -> new UsernameNotFoundException("Can't find user"));

        if (memberRepository.existsByUserAndTeam(user, team))
            throw new IllegalArgumentException("The user is already in the team");

        createMember(user, team, "Member");
    }

    @Override
    public void changeStage(String teamName, Map<String, String> request, Principal connectedUser) {
        var team = Team.findTeamByName(repository, teamName);
        var user = Member.getActionPerformer(memberRepository, connectedUser, team);

        if (user.checkPermission())
            throw new IllegalStateException("You do not have permission for this action");

        StageType currentStage = team.getCurrentStage().getType();

        var stages = StageType.values();
        int currentIndex = currentStage.ordinal();

        if (currentIndex < stages.length - 1) {
            StageType nextStage = stages[currentIndex + 1];
            teamStageRepository.delete(team.getCurrentStage());
            TeamStage.create(teamStageRepository, team, nextStage, request.get("dueDate"));

            // Дії для переходу

        } else {
            throw new IllegalStateException("This is the last stage");
        }

    }

    @Override
    public List<TeamResponse> getAll() {
        var teams = repository.findAll();
        return teams.stream()
                .map(converter::mapTeamToTeamResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamResponse> findByUser(Principal connectedUser) {
        var user = User.getUserFromPrincipal(connectedUser);
        List<Member> members = memberRepository.findAllByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Can't find"));

        return members.stream()
                .map(member -> converter.mapTeamToTeamResponse(member.getTeam()))
                .collect(Collectors.toList());
    }

    @Override
    public TeamResponse findByName(String name) {
        var team = Team.findTeamByName(repository, name);
        return converter.mapTeamToTeamResponse(team);
    }

    private void createMember(User user, Team team, String role) {
        memberRepository.save(
                Member.builder()
                        .user(user)
                        .role(RoleInTeam.valueOf(role.toUpperCase()))
                        .team(team)
                        .isEmailsAllowed(true)
                        .build()
        );
    }
}
