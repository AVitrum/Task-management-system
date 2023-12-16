package com.vitrum.api.services.implementations;

import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.enums.TaskCategory;
import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.models.User;
import com.vitrum.api.data.request.TaskRequest;
import com.vitrum.api.data.submodels.OldTask;
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
        Member creator = Member.getActionPerformer(
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
    public void addPerformer(String teamName, String taskTitle, Principal connectedUser, String performerName) {
        Task task = getTask(teamName, taskTitle);
        Member performer = findMemberByUsernameAndTeam(performerName, task.getTeam());
        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, performer.getTeam());

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
    public void update(String teamName, String taskTitle, Principal connectedUser, TaskRequest request) {
        Task task = getTask(teamName, taskTitle);
        Member member = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (!member.equals(task.getCreator())
                && !member.equals(task.getPerformer())
                && member.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");

        saveHistory(task);
        updateTaskFields(request, task);
    }

    private void updateTaskFields(TaskRequest request, Task task) {
        if (request.getDescription() != null)
            task.setDescription(request.getDescription());
        if (request.getStatus() != null)
            task.setStatus(Status.valueOf(request.getStatus().toUpperCase()));

        task.saveWithChangeDate(repository);
    }

    @Override
    public String changeCategory(Map<String, String> request, String teamName, String taskTitle, Principal connectedUser) {
        Task task = getTask(teamName, taskTitle);
        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (!actionPerformer.equals(task.getCreator())
                && !actionPerformer.equals(task.getPerformer())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");

        TaskCategory category = TaskCategory.valueOf(request.get("category").toUpperCase());

        String message;

        if (!task.getCategories().contains(category)) {
            task.getCategories().remove(category);
            message = "Added";
        } else {
            task.getCategories().add(category);
            message = "Removed";
        }

        task.saveWithChangeDate(repository);
        return message;
    }

    @Override
    public List<TaskResponse> findAll(String teamName, Principal connectedUser) {
        List<Task> tasks = repository.findAllByTeam(Team.findTeamByName(teamRepository, teamName));
        return tasks.stream().map(converter::mapTaskToTaskResponse).collect(Collectors.toList());
}

    @Override
    public LocalDateTime getDeadlineForTasks(String teamName) {
        Team team = Team.findTeamByName(teamRepository, teamName);
        return team.getCurrentStage().getDueDate();
    }

    @Override
    public Task findByTitle(String teamName, String taskTitle, Principal connectedUser) {
        return getTask(teamName, taskTitle);
    }

    @Override
    public void deleteByTitle(String teamName, String taskTitle, Principal connectedUser) {
        Task task = findByTitle(teamName, taskTitle, connectedUser);
        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (actionPerformer.checkPermission())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        if (task.getStatus() == Status.DELETED)
            throw new IllegalArgumentException("The task has already been deleted and cannot be deleted again");

        saveHistory(task);

        task.setStatus(Status.DELETED);
        repository.save(task);
    }

    private void saveHistory(Task task) {
        OldTask oldTask = converter.mapTaskToOldTask(task);
        oldTaskRepository.save(oldTask);
        commentRepository.saveAll(oldTask.getComments());
        commentRepository.deleteAll(task.getComments());
        task.setComments(new ArrayList<>());
    }

    private Member findMemberByUsernameAndTeam(String username, Team team) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
    }

    private Task getTask(String teamName, String taskTitle) {
        return Task.findTask(
                repository,
                Team.findTeamByName(teamRepository, teamName),
                taskTitle
        );
    }

}
