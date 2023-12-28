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
import java.util.Comparator;
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
    private final Converter converter;
    private final MessageUtil messageUtil;


    @Override
    public void create(Long teamId, Principal connectedUser, TaskRequest request) {
        Member creator = Member.getActionPerformer(
                memberRepository,
                connectedUser,
                Team.findTeamById(teamRepository, teamId)
        );

        if (creator.checkPermission())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        checkTitle(request);

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

        ifDeletedOrCompleted(task);

        Member performer = findMemberByUsernameAndTeam(performerName, task.getTeam());
        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, performer.getTeam());

        checkManager(actionPerformer, task);

        task.setPerformer(performer);
        if (performer.equals(actionPerformer))
            task.setStatus(Status.PENDING);
        else
            task.setStatus(Status.ASSIGNED);
        task.setAssignmentDate(LocalDateTime.now());

        saveHistory(task, String.format(
                "%s appointed %s as the performer",
                actionPerformer.getUser().getTrueUsername(),
                performer.getUser().getTrueUsername()),
                actionPerformer.getUser()
        );

        repository.save(task);

        messageUtil.sendMessage(
                task.getPerformer(),
                "TMS Info!", String.format(
                        "Team: %s\n" +
                                "New tasks have been added to you ", task.getTeam().getName()
                )
        );
    }

    @Override
    public String confirmTask(Long teamId, Long taskId, Principal connectedUser) {
        Task task = getTask(teamId, taskId);

        ifDeletedOrCompleted(task);

        if (task.getTeam().getCurrentStage(teamStageRepository).getType().equals(StageType.REQUIREMENTS)) {
            throw new IllegalStateException("You cannot mark a task as completed during this stage.");
        }

        if (task.getStatus().equals(Status.PENDING))
            throw new IllegalArgumentException("First, add the performer");

        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (!actionPerformer.equals(task.getPerformer())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");

        Boolean currentStatus = task.getCompleted();

        if (!currentStatus
                && !task.getStatus().equals(Status.UNCOMPLETED)
                && task.getTeam().getCurrentStage(teamStageRepository).getType().equals(StageType.REVIEW)
        ) task.setStatus(Status.OVERDUE);

        if (task.getStatus().equals(Status.UNCOMPLETED))
            task.setStatus(Status.IN_REVIEW);

        task.setCompleted(!currentStatus);
        task.saveWithChangeDate(repository);

        String message = task.getCompleted() ? "Marked as COMPLETED" : "Marked as NOT COMPLETED";

        messageUtil.sendMessage(
                actionPerformer,
                String.format("%s %s by %s", task.getTitle(), message, actionPerformer.getUser().getTrueUsername()),
                ""
        );

        return message;
    }

    @Override
    public void update(Long teamId, Long taskId, Principal connectedUser, TaskRequest request) {
        Task task = getTask(teamId, taskId);

        ifDeletedOrCompleted(task);

        Member member = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        checkManager(member, task);

        if (task.getCompleted() && request.getStatus() == null)
            throw new IllegalStateException("Task already marked as completed");

        verifyStatus(request, task);

        saveHistory(task, String.format("%s updated the task", member.getUser().getTrueUsername()), member.getUser());
        updateTaskFields(request, task);

        repository.save(task);

        messageUtil.sendMessage(task.getPerformer(), task.getTeam().getName() + " Info!",
                "Task has been updated: " + task.getTitle());
    }

    @Override
    public String changeCategory(Map<String, String> request, Long teamId, Long taskId, Principal connectedUser) {
        Task task = getTask(teamId, taskId);

        ifDeletedOrCompleted(task);

        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        checkManager(actionPerformer, task);

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

        List<Task> sortedTasks = tasks.stream().sorted(Comparator.comparing(Task::getId)).toList();

        return sortedTasks.stream()
                .map(converter::mapTaskToTaskResponse)
                .collect(Collectors.toList());
    }


    @Override
    public List<TaskResponse> findAllInReview(Long teamId, Principal connectedUser) {
        return findAll(teamId, connectedUser).stream()
                .filter(taskResponse -> taskResponse.getStatus().equals(Status.IN_REVIEW.name())).toList();
    }

    @Override
    public Task findById(Long teamId, Long taskId) {
        return getTask(teamId, taskId);
    }

    @Override
    public void restoreById(Long taskId, Long teamId, Principal connectedUser) {
        Task task = Task.findTask(repository, Team.findTeamById(teamRepository, teamId), taskId);
        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        checkManager(actionPerformer, task);

        if (task.getStatus().equals(Status.DELETED)) {
            saveHistory(
                    task,
                    String.format("%s restored the task", actionPerformer.getUser().getTrueUsername()),
                    actionPerformer.getUser()
            );
            task.setStatus(task.getPerformer().equals(task.getCreator()) ? Status.PENDING : Status.ASSIGNED);
            repository.save(task);
        } else
            throw new IllegalStateException("Task wasn't delete");

        messageUtil.sendMessage(task.getPerformer(), task.getTeam().getName() + " Info!",
                "The task has been restored: " + task.getTitle());
    }

    @Override
    public void deleteByTitle(Long teamId, Long taskId, Principal connectedUser) {
        Task task = findById(teamId, taskId);
        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (actionPerformer.checkPermission())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        ifDeletedOrCompleted(task);

        saveHistory(
                task,
                String.format("%s deleted the task", actionPerformer.getUser().getTrueUsername()),
                actionPerformer.getUser()
        );

        if (task.getCompleted())
            task.setCompleted(false);

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

        task.saveWithChangeDate(repository);
    }

    private static void checkManager(Member member, Task task) {
        if (!member.equals(task.getCreator())
                && member.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");
    }

    private void verifyStatus(TaskRequest request, Task task) {
        if (request.getStatus() != null) {
            boolean isCurrentStageReview = task.getTeam().getCurrentStage(teamStageRepository)
                    .getType().equals(StageType.REVIEW);

            boolean isApprovalMethod = request.getStatus().toUpperCase().equals(Status.UNCOMPLETED.name())
                    || request.getStatus().toUpperCase().equals(Status.COMPLETED.name());

            if (task.getCompleted() && !isApprovalMethod)
                throw new IllegalStateException("Task already marked as completed");

            if (task.getStatus().equals(Status.PENDING) && isApprovalMethod)
                throw new IllegalArgumentException("First, add the performer");

            if (isApprovalMethod && !isCurrentStageReview)
                throw new IllegalStateException("These status is only allowed during the review");

            if (Status.valueOf(request.getStatus().toUpperCase()).equals(Status.COMPLETED)
                    && !task.getCompleted()
                    && isCurrentStageReview
            ) throw new IllegalArgumentException("The task must be marked as completed");

            if (request.getStatus().toUpperCase().equals(Status.UNCOMPLETED.name()))
                task.setCompleted(false);
        }
    }

    private void saveHistory(Task task, String message, User user) {
        OldTask oldTask = converter.mapTaskToOldTask(task, message, user);
        oldTaskRepository.save(oldTask);
        task.setVersion(task.getVersion() + 1);
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

    private void ifDeletedOrCompleted(Task task) {
        if (task.getStatus().equals(Status.DELETED))
            throw new IllegalStateException("The task has been deleted");
        if (task.getStatus().equals(Status.COMPLETED))
            throw new IllegalStateException("The task has been completed");
    }

    private static void checkTitle(TaskRequest request) {
        if (request.getTitle().replaceAll("\\s", "").isEmpty())
            throw new IllegalArgumentException("The task name cannot be empty");
        if (request.getTitle().length() > 150) {
            throw new IllegalArgumentException("The team name cannot be more than 150 characters (including spaces)");
        }
    }
}
