package com.vitrum.api.services.implementation;

import com.vitrum.api.data.request.TaskRequest;
import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.models.User;
import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.*;
import com.vitrum.api.services.interfaces.TaskService;
import com.vitrum.api.util.Converter;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    private final UserRepository userRepository;
    private final Converter converter;
    private final MessageUtil messageUtil;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void create(TaskRequest request, String teamName, String bundleName) {
        var creator = findCreator(teamName);

        if (creator.checkPermissionToCreate())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        var bundle = findBundle(bundleName, creator);

        if (repository.existsByTitleAndBundle(request.getTitle(), bundle)) {
            throw new IllegalArgumentException("A task with that name already exists in this team");
        }

        var task = createTask(request, bundle);
        repository.save(task);
    }

    @Override
    public void change(TaskRequest request, String taskTitle, String teamName,String bundleName) {
        var creator = findCreator(teamName);
        var bundle = findBundle(bundleName, creator);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        if (task.getStatus() == Status.DELETED) {
            throw new IllegalArgumentException("The task is not available for modification as it has been deleted");
        }

        OldTask oldTask = converter.mapTaskToOldTask(task);
        oldTaskRepository.save(oldTask);

        updateTaskFields(request, task);
        repository.save(task);

        messageUtil.sendMessage(bundle.getPerformer(), task.toString(), "The task has been changed");
    }

    @Override
    public void delete(String taskTitle, String teamName, String bundleName) {
        var creator = findCreator(teamName);
        var bundle = findBundle(bundleName, creator);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        if (task.getStatus() == Status.DELETED) {
            throw new IllegalArgumentException("The task has already been deleted and cannot be deleted again");
        }

        task.setStatus(Status.DELETED);
        task.setVersion(task.getVersion());
        repository.save(task);

        var oldTask = converter.mapTaskToOldTask(task);
        oldTaskRepository.save(oldTask);

        messageUtil.sendMessage(task.getBundle().getPerformer(), task.toString(), "The task has been deleted");
    }

    @Override
    public Task getTask(String taskTitle, String creatorName, String teamName, String bundleName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var user = userRepository.findByUsername(creatorName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var creator = memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        var bundle = findBundle(bundleName, creator);

        return repository.findByTitleAndBundle(taskTitle, bundle)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    private Bundle findBundle(String bundleName, Member creator) {
        return bundleRepository.findByCreatorAndTitle(creator, bundleName)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
    }

    private Member findMemberByUsernameAndTeam(String username, String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
    }

    private Member findCreator(String teamName) {
        var user = User.getAuthUser(userRepository);
        return findMemberByUsernameAndTeam(user.getTrueUsername(), teamName);
    }

    private Member findPerformer(String performer, String teamName) {
        return findMemberByUsernameAndTeam(performer, teamName);
    }


    private Task createTask(TaskRequest request, Bundle bundle) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .version(0L)
                .creationTime(LocalDateTime.now())
                .status(Status.PENDING)
                .dueDate(LocalDateTime.parse(request.getDueDate(), formatter))
                .bundle(bundle)
                .build();
    }

    private Task findTaskByTitleAndBundle(String taskTitle, Bundle bundle) {
        return repository.findByTitleAndBundle(taskTitle, bundle)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
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
