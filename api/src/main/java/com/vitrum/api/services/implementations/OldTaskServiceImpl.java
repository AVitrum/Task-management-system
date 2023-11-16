package com.vitrum.api.services.implementations;

import com.vitrum.api.models.submodels.OldTask;
import com.vitrum.api.repositories.OldTaskRepository;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.dto.Response.HistoryResponse;
import com.vitrum.api.models.Bundle;
import com.vitrum.api.repositories.BundleRepository;
import com.vitrum.api.models.Member;
import com.vitrum.api.repositories.MemberRepository;
import com.vitrum.api.models.enums.Status;
import com.vitrum.api.models.Task;
import com.vitrum.api.repositories.TaskRepository;
import com.vitrum.api.repositories.TeamRepository;
import com.vitrum.api.services.OldTaskService;
import com.vitrum.api.services.TaskService;
import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OldTaskServiceImpl implements OldTaskService {

    private final OldTaskRepository repository;
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final BundleRepository bundleRepository;
    private final TaskService taskService;
    private final Converter converter;

    @Override
    public List<HistoryResponse> findAllByTitle(String taskTitle, String creatorName, String teamName, String bundleName) {
        var creator = findCreator(creatorName, teamName);
        var bundle = findBundle(bundleName, creator);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        List<OldTask> oldTasks = getOldTasks(task);

        return oldTasks.stream()
                .map(converter::mapOldTaskToHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
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

    @Override
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

    @Override
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
