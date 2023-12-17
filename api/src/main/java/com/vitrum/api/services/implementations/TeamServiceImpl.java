package com.vitrum.api.services.implementations;

import com.vitrum.api.data.enums.StageType;
import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.submodels.TeamStage;
import com.vitrum.api.repositories.*;
import com.vitrum.api.data.response.*;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.request.TeamCreationRequest;
import com.vitrum.api.data.models.User;
import com.vitrum.api.services.interfaces.TeamService;
import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository repository;
    private final MemberRepository memberRepository;
    private final TeamStageRepository teamStageRepository;
    private final TaskRepository taskRepository;
    private final Converter converter;

    @Override
    public TeamCreationResponse create(TeamCreationRequest request, Principal connectedUser) {
        try {
            User user = User.getUserFromPrincipal(connectedUser);
            Team team = Team.builder()
                    .name(request.getName().replaceAll("\\s", "_"))
                    .members(new ArrayList<>())
                    .build();
            repository.save(team);

            TeamStage.create(teamStageRepository, team, StageType.PREPARATION, null);
            Member.create(memberRepository, user, team, "Leader");

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
    public void changeStage(String teamName, Map<String, String> request, Principal connectedUser) {
        Team team = Team.findTeamByName(repository, teamName);
        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, team);

        if (actionPerformer.checkPermission())
            throw new IllegalStateException("You do not have permission for this action");

        if (!LocalDateTime.now().isAfter(team.getCurrentStage().getDueDate()))
            throw new IllegalStateException("It is impossible to move to the next " +
                    "stage until the current one is completed");

        List<Task> tasks = team.getTasks();
        tasks.stream().filter(task -> task.getStatus().equals(Status.APPROVED))
                .forEach(task -> task.setStatus(Status.NOW_UNAVAILABLE));
        taskRepository.saveAll(tasks);

        changeStage(request, team);
    }

    @Override
    public List<TeamResponse> getAll() {
        List<Team> teams = repository.findAll();
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
        return converter.mapTeamToTeamResponse(Team.findTeamByName(repository, name));
    }

    private void changeStage(Map<String, String> request, Team team) {
        StageType currentStage = team.getCurrentStage().getType();
        var stages = StageType.values();
        int currentIndex = currentStage.ordinal();
        if (currentIndex < stages.length - 1) {
            StageType nextStage = stages[currentIndex + 1];
            teamStageRepository.delete(team.getCurrentStage());
            TeamStage.create(teamStageRepository, team, nextStage, request.get("dueDate"));
        } else {
            throw new IllegalStateException("This is the last stage");
        }
    }
}
