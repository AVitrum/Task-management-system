package com.vitrum.api.services.implementations;

import com.vitrum.api.data.enums.StageType;
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
    private final TeamStageRepository teamStageRepository;
    private final MessageUtil messageUtil;
    private final Converter converter;


    @Override
    public void create(Long teamId, Principal connectedUser, TaskRequest request) {
        Member creator = Member.getActionPerformer(
                memberRepository,
                connectedUser,
                Team.findTeamById(teamRepository, teamId)
        );

        if (creator.checkPermission())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        repository.save(
                Task.builder()
                        .creator(creator)
                        .performer(creator)
                        .team(creator.getTeam())
                        .title(request.getTitle().replaceAll("\\s", "_"))
                        .description(request.getDescription())
                        .status(Status.PENDING)
                        .version(0L)
                        .completed(false)
                        .assignmentDate(LocalDateTime.now())
                        .changeTime(LocalDateTime.now())
                        .build()
        );
    }

    @Override
    public void addPerformer(Long teamId, Long taskId, Principal connectedUser, String performerName) {
        Task task = getTask(teamId, taskId);

        ifDeleted(task);

        Member performer = findMemberByUsernameAndTeam(performerName, task.getTeam());
        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, performer.getTeam());

        if (!actionPerformer.equals(task.getCreator())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");

        task.setPerformer(performer);
        if (performer.equals(actionPerformer))
            task.setStatus(Status.PENDING);
        else
            task.setStatus(Status.ASSIGNED);
        task.setAssignmentDate(LocalDateTime.now());
        repository.save(task);

        messageUtil.sendMessage(
                performer,
                "TMS Info!", String.format(
                        "Team: %s\n" +
                                "New tasks have been added to you by %s", task.getTeam().getName(),
                        actionPerformer.getUser().getEmail()
                )
        );
    }

    @Override
    public String confirmTask(Long teamId, Long taskId, Principal connectedUser) {
        Task task = getTask(teamId, taskId);

        ifDeleted(task);

        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (!actionPerformer.equals(task.getPerformer())
                && !actionPerformer.equals(task.getCreator())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");

        Boolean currentStatus = task.getCompleted();

        if (!currentStatus && task.getStatus().equals(Status.UNCOMPLETED))
            task.setStatus(Status.OVERDUE);

        task.setCompleted(!currentStatus);
        task.saveWithChangeDate(repository);

        return task.getCompleted() ? "Marked as COMPLETED" : "You have marked a task as NOT COMPLETED";
    }

    @Override
    public void update(Long teamId, Long taskId, Principal connectedUser, TaskRequest request) {
        Task task = getTask(teamId, taskId);

        ifDeleted(task);

        Member member = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (!member.equals(task.getCreator())
                && member.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");

        saveHistory(task);
        updateTaskFields(request, task);

        if (task.getTeam().getCurrentStage(teamStageRepository).getType().equals(StageType.REVIEW)
                || !task.getStatus().equals(Status.APPROVED)) return;

        task.setStatus(Status.ASSIGNED);
        task.setCompleted(false);
        repository.save(task);

        messageUtil.sendMessage(task.getPerformer(), task.getTeam().getName() + " Info!",
                "Task has been updated: " + task.getTitle());
    }

    @Override
    public String changeCategory(Map<String, String> request, Long teamId, Long taskId, Principal connectedUser) {
        Task task = getTask(teamId, taskId);

        ifDeleted(task);

        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (!actionPerformer.equals(task.getCreator())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");

        TaskCategory category = TaskCategory.valueOf(request.get("category").toUpperCase());

        String message;

        if (!task.getCategories().contains(category)) {
            task.getCategories().add(category);
            message = "Added";
        } else {
            task.getCategories().remove(category);
            message = "Removed";
        }

        task.saveWithChangeDate(repository);
        return message;
    }

    @Override
    public List<TaskResponse> findAll(Long teamId, Principal connectedUser) {
        List<Task> tasks = repository.findAllByTeam(Team.findTeamById(teamRepository, teamId));
        return tasks.stream().map(converter::mapTaskToTaskResponse).collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> findAllInReview(Long teamId, Principal connectedUser) {
        return findAll(teamId, connectedUser).stream()
                .filter(taskResponse -> taskResponse.getStatus().equals(Status.IN_REVIEW.name())).toList();
    }

    @Override
    public Task findByTitle(Long teamId, Long taskId, Principal connectedUser) {
        return getTask(teamId, taskId);
    }

    @Override
    public void restoreByTitle(Long taskTitle, Long teamId, Principal connectedUser) {
        Task task = Task.findTask(repository, Team.findTeamById(teamRepository, teamId), taskTitle);
        checkPermission(connectedUser, task);

        if (task.getStatus().equals(Status.DELETED)) {
            saveHistory(task);
            task.setStatus(task.getPerformer().equals(task.getCreator()) ? Status.PENDING : Status.ASSIGNED);
            task.setVersion(task.getVersion() + 1);
            System.out.println(task.getVersion());
            repository.save(task);
        } else throw new IllegalStateException("Task wasn't delete");

        messageUtil.sendMessage(task.getPerformer(), task.getTeam().getName() + " Info!",
                "The task has been restored: " + task.getTitle());
    }

    @Override
    public void deleteByTitle(Long teamId, Long taskId, Principal connectedUser) {
        Task task = findByTitle(teamId, taskId, connectedUser);
        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (actionPerformer.checkPermission())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        if (task.getStatus() == Status.DELETED)
            throw new IllegalArgumentException("The task has already been deleted and cannot be deleted again");

        saveHistory(task);

        task.setVersion(task.getVersion() + 1);
        task.setStatus(Status.DELETED);
        repository.save(task);
    }

    private void updateTaskFields(TaskRequest request, Task task) {
        if (request.getTitle() != null)
            task.setTitle(request.getTitle().replaceAll("\\s", "_"));
        if (request.getDescription() != null)
            task.setDescription(request.getDescription());
        if (request.getStatus() != null)
            task.setStatus(Status.valueOf(request.getStatus().toUpperCase()));

        task.setVersion(task.getVersion() + 1L);
        task.saveWithChangeDate(repository);
    }

    private void saveHistory(Task task) {
        OldTask oldTask = converter.mapTaskToOldTask(task);
        oldTaskRepository.save(oldTask);
    }

    private Member findMemberByUsernameAndTeam(String username, Team team) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
    }

    private Task getTask(Long teamId, Long taskId) {
        return Task.findTask(
                repository,
                Team.findTeamById(teamRepository, teamId),
                taskId
        );
    }

    private void checkPermission(Principal connectedUser, Task task) {
        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (!actionPerformer.equals(task.getCreator())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");
    }

    private static void ifDeleted(Task task) {
        if (task.getStatus().equals(Status.DELETED))
            throw new IllegalStateException("The task has been deleted");
    }
}
