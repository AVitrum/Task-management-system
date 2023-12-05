package com.vitrum.api.services.implementations;

import com.vitrum.api.data.models.*;
import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.*;
import com.vitrum.api.data.request.TaskRequest;
import com.vitrum.api.repositories.TeamRepository;
import com.vitrum.api.services.interfaces.TaskService;
import com.vitrum.api.util.Converter;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final OldTaskRepository oldTaskRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final BundleRepository bundleRepository;
    private final Converter converter;
    private final MessageUtil messageUtil;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void add(TaskRequest request, Principal connectedUser, String team, String bundleTitle) {
        var bundle = findBundle(Team.findTeamByName(teamRepository, team), bundleTitle);
        var actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, bundle.getTeam());

        if (actionPerformer.checkPermission())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        if (repository.existsByTitleAndBundle(request.getTitle(), bundle))
            throw new IllegalArgumentException("A task with that name already exists in this team");

        repository.save(createTask(request, bundle));
    }

    @Override
    public void change(
            TaskRequest request,
            String taskTitle,
            Principal connectedUser,
            String team,
            String bundleTitle
    ) {
        var bundle = findBundle(Team.findTeamByName(teamRepository, team), bundleTitle);
        var task = Task.findTaskByTitleAndBundle(repository, taskTitle, bundle);
        var actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, bundle.getTeam());

        if (actionPerformer.checkPermission())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        if (task.getStatus() == Status.DELETED)
            throw new IllegalArgumentException("The task is not available for modification as it has been deleted");

        OldTask oldTask = converter.mapTaskToOldTask(task);
        oldTaskRepository.save(oldTask);

        updateTaskFields(request, task);
        repository.save(task);

        messageUtil.sendMessage(
                bundle.getPerformer(),
                String.format("The task has been changed by %s", actionPerformer.getUser().getEmail()),
                task.toString()
        );
    }

    @Override
    public Task getTask(String taskTitle, Principal connectedUser, String team, String bundleTitle) {
        var bundle = findBundle(Team.findTeamByName(teamRepository, team), bundleTitle);
        var actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, bundle.getTeam());

        if (!actionPerformer.equals(bundle.getCreator())
                && !actionPerformer.equals(bundle.getPerformer())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You cannot view other users' task");

        return Task.findTaskByTitleAndBundle(repository, taskTitle, bundle);
    }

    @Override
    public void delete(String taskTitle, Principal connectedUser, String team, String bundleTitle) {
        var bundle = findBundle(Team.findTeamByName(teamRepository, team), bundleTitle);
        var task = Task.findTaskByTitleAndBundle(repository, taskTitle, bundle);
        var actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, bundle.getTeam());

        if (actionPerformer.checkPermission())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        if (task.getStatus() == Status.DELETED)
            throw new IllegalArgumentException("The task has already been deleted and cannot be deleted again");

        task.setStatus(Status.DELETED);
        task.setVersion(task.getVersion());
        repository.save(task);

        var oldTask = converter.mapTaskToOldTask(task);
        oldTaskRepository.save(oldTask);

        messageUtil.sendMessage(
                task.getBundle().getPerformer(),
                String.format("The task has been deleted by %s", actionPerformer.getUser().getEmail()),
                task.toString()
        );
    }

    private Bundle findBundle(Team team, String title) {
        return bundleRepository.findByTeamAndTitle(team, title)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
    }

    private Task createTask(TaskRequest request, Bundle bundle) {
        return Task.builder()
                .title(request.getTitle().replaceAll("\\s", "_"))
                .description(request.getDescription())
                .priority(request.getPriority())
                .version(0L)
                .creationTime(LocalDateTime.now())
                .status(Status.PENDING)
                .dueDate(LocalDateTime.parse(request.getDueDate(), formatter))
                .bundle(bundle)
                .build();
    }

    private void updateTaskFields(TaskRequest request, Task task) {
        if (request.getDescription() != null)
            task.setDescription(request.getDescription());
        if (request.getPriority() != null)
            task.setPriority(request.getPriority());
        if (request.getDueDate() != null)
            task.setDueDate(LocalDateTime.parse(request.getDueDate(), formatter));
        if (request.getStatus() != null)
            task.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
        task.setVersion(task.getVersion() + 1);
    }
}
