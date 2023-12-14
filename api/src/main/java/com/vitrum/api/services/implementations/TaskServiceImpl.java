package com.vitrum.api.services.implementations;

import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.enums.TaskCategory;
import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.request.TaskRequest;
import com.vitrum.api.repositories.*;
import com.vitrum.api.data.response.TaskResponse;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.services.interfaces.TaskService;
import com.vitrum.api.util.Converter;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final OldTaskRepository oldTaskRepository;
    private final CommentRepository commentRepository;
    private final MessageUtil messageUtil;
    private final Converter converter;


    @Override
    public void create(String teamName, Principal connectedUser, TaskRequest request) {
        var creator = Member.getActionPerformer(
                memberRepository,
                connectedUser,
                Team.findTeamByName(teamRepository, teamName)
        );

        if (creator.checkPermission())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        if (repository.existsByTitleAndTeam(request.getTitle(), creator.getTeam()))
            throw new IllegalArgumentException("Task with the same name already exists");

        repository.save(
                Task.builder()
                        .creator(creator)
                        .performer(creator)
                        .team(creator.getTeam())
                        .title(request.getTitle().replaceAll("\\s", "_"))
                        .description(request.getDescription())
                        .status(Status.PENDING)
                        .version(0L)
                        .assignmentDate(LocalDateTime.now())
                        .changeTime(LocalDateTime.now())
                        .build()
        );
    }

    @Override
    public void addPerformer(String teamName, String bundleTitle, Principal connectedUser, String performerName) {
        var performer = findMemberByUsernameAndTeam(performerName, teamName);

        var task = Task.findTask(
                repository,
                performer.getTeam(),
                bundleTitle
        );

        var actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, performer.getTeam());

        if (!actionPerformer.equals(task.getCreator())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");

        task.setPerformer(performer);
        task.setAssignmentDate(LocalDateTime.now());
        repository.save(task);

        messageUtil.sendMessage(
                performer,
                "TMS Info!", String.format(
                        "Team: %s\n" +
                                "New tasks have been added to you by %s", teamName, actionPerformer.getUser().getEmail()
                )
        );
    }

    @Override
    public String changeCategory(Map<String, String> request, String teamName, String bundleTitle, Principal connectedUser) {
        var task = Task.findTask(
                repository,
                Team.findTeamByName(teamRepository, teamName),
                bundleTitle
        );
        var actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (!actionPerformer.equals(task.getCreator())
                && !actionPerformer.equals(task.getPerformer())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");

        var category = TaskCategory.valueOf(request.get("category").toUpperCase());

        String message;

        if (!task.getCategories().contains(category)) {
            task.getCategories().remove(category);
            message = "Added";
        } else {
            task.getCategories().add(category);
            message = "Removed";
        }

        task.saveChangeDate(repository);
        return message;
    }

    @Override
    public List<TaskResponse> findAll(String teamName, Principal connectedUser) {
        List<Task> tasks = repository.findAllByTeam(Team.findTeamByName(teamRepository, teamName));
        return tasks.stream().map(converter::mapTaskToTaskResponse).collect(Collectors.toList());
}

    @Override
    public LocalDateTime getDeadlineForTasks(String teamName) {
        var team = Team.findTeamByName(teamRepository, teamName);
        return team.getCurrentStage().getDueDate();
    }

    @Override
    public Task findByTitle(String teamName, String taskTitle, Principal connectedUser) {
        return Task.findTask(
                repository,
                Team.findTeamByName(teamRepository, teamName),
                taskTitle
        );
    }

    @Override
    public void deleteByTitle(String teamName, String bundleTitle, Principal connectedUser) {
        Task task = findByTitle(teamName, bundleTitle, connectedUser);

        var actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (actionPerformer.checkPermission())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        if (task.getStatus() == Status.DELETED)
            throw new IllegalArgumentException("The task has already been deleted and cannot be deleted again");

        task.setStatus(Status.DELETED);
        repository.save(task);

        var oldTask = converter.mapTaskToOldTask(task);
        oldTaskRepository.save(oldTask);
        commentRepository.saveAll(oldTask.getComments());

        commentRepository.deleteAll(task.getComments());
        task.setComments(new ArrayList<>());
        repository.save(task);
    }

    private Member findMemberByUsernameAndTeam(String username, String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
    }

}
