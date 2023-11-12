package com.vitrum.api.manager.task.history;

import com.vitrum.api.credentials.user.UserRepository;
import com.vitrum.api.dto.Response.HistoryResponse;
import com.vitrum.api.manager.bundle.Bundle;
import com.vitrum.api.manager.bundle.BundleRepository;
import com.vitrum.api.manager.member.Member;
import com.vitrum.api.manager.member.MemberRepository;
import com.vitrum.api.manager.task.main.Status;
import com.vitrum.api.manager.task.main.Task;
import com.vitrum.api.manager.task.main.TaskRepository;
import com.vitrum.api.manager.task.main.TaskService;
import com.vitrum.api.manager.team.TeamRepository;
import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OldTaskService {
    private final OldTaskRepository repository;
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final BundleRepository bundleRepository;
    private final TaskService taskService;
    private final Converter converter;

    public List<HistoryResponse> findAllByTitle(String taskTitle, String creatorName, String teamName, String bundleName) {
        var creator = findCreator(creatorName, teamName);
        var bundle = findBundle(bundleName, creator);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        List<OldTask> oldTasks = getOldTasks(task);

        return oldTasks.stream()
                .map(converter::mapOldTaskToHistoryResponse)
                .collect(Collectors.toList());
    }

    public OldTask getByVersion(
            String taskTitle,
            String creatorName,
            String teamName,
            String bundleName,
            Long version
    ) {
        var creator = findCreator(creatorName, teamName);
        var bundle = findBundle(bundleName, creator);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        return repository.findByTaskAndVersion(task, version)
                .orElseThrow(() -> new IllegalArgumentException("Task version not found"));
    }

    public void restore(
            String taskTitle,
            String creatorName,
            String teamName,
            String bundleName,
            Long version
    ) {
        OldTask oldTask = getByVersion(taskTitle, creatorName, teamName, bundleName, version);

        var creator = findCreator(creatorName, teamName);
        var bundle = findBundle(bundleName, creator);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        List<OldTask> oldTasks = getOldTasks(task);
        repository.deleteAll(oldTasks.subList(version.intValue(), oldTasks.size()));

        updateTaskFields(oldTask, task);

        task.setStatus(Status.RESTORED);
        taskRepository.save(task);
    }

    public void delete(String taskTitle, String creatorName, String teamName, String bundleName) {
        var creator = findCreator(creatorName, teamName);
        var bundle = findBundle(bundleName, creator);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        List<OldTask> oldTasks = getOldTasks(task);
        repository.deleteAll(oldTasks);

        taskRepository.delete(taskService.getTask(taskTitle, creatorName, teamName, bundleName));
    }

    private Task findTaskByTitleAndBundle(String taskTitle, Bundle bundle) {
        return taskRepository.findByTitleAndBundle(taskTitle, bundle)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    private List<OldTask> getOldTasks(Task task) {
        return repository.findAllByTask(task)
                .orElseThrow(() -> new IllegalArgumentException("Wrong task title"));
    }
    private Bundle findBundle(String bundleName, Member creator) {
        return bundleRepository.findByCreatorAndTitle(creator, bundleName)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
    }

    private Member findCreator(String creatorName, String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var user = userRepository.findByUsername(creatorName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
    }

    private void updateTaskFields(OldTask oldTask, Task task) {
        task.setTitle(oldTask.getTitle());
        task.setPriority(oldTask.getPriority());
        task.setDescription(oldTask.getDescription());
        task.setVersion(oldTask.getVersion());
        task.setDueDate(oldTask.getDueDate());
        task.setStatus(oldTask.getStatus());
    }
}
