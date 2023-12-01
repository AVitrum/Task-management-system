package com.vitrum.api.services.implementations;

import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.OldTaskRepository;
import com.vitrum.api.data.response.HistoryResponse;
import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.repositories.BundleRepository;
import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.models.Task;
import com.vitrum.api.repositories.TaskRepository;
import com.vitrum.api.repositories.TeamRepository;
import com.vitrum.api.services.interfaces.OldTaskService;
import com.vitrum.api.util.Converter;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OldTaskServiceImpl implements OldTaskService {

    private final OldTaskRepository repository;
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final BundleRepository bundleRepository;
    private final MessageUtil messageUtil;
    private final Converter converter;

    @Override
    public List<HistoryResponse> findAllByTitle(String taskTitle, String teamName, String bundleName) {
        var bundle = findBundle(findTeam(teamName), bundleName);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        List<OldTask> oldTasks = getOldTasks(task);

        return oldTasks.stream()
                .map(converter::mapOldTaskToHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OldTask getByVersion(
            String taskTitle,
            String teamName,
            String bundleTitle,
            Long version
    ) {
        var bundle = findBundle(findTeam(teamName), bundleTitle);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        return repository.findByTaskAndVersion(task, version)
                .orElseThrow(() -> new IllegalArgumentException("Task version not found"));
    }

    @Override
    public void restore(
            String taskTitle,
            String teamName,
            String bundleTitle,
            Long version
    ) {
        OldTask oldTask = getByVersion(taskTitle, teamName, bundleTitle, version);

        var bundle = findBundle(findTeam(teamName), bundleTitle);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        List<OldTask> oldTasks = getOldTasks(task);
        repository.deleteAll(oldTasks.subList(version.intValue(), oldTasks.size()));

        updateTaskFields(oldTask, task);

        task.setStatus(Status.RESTORED);
        taskRepository.save(task);

        messageUtil.sendMessage(bundle.getPerformer(), "The task has been restored", task.toString());
    }

    @Override
    public void delete(String taskTitle, String teamName, String bundleTitle) {
        var bundle = findBundle(findTeam(teamName), bundleTitle);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        List<OldTask> oldTasks = getOldTasks(task);
        repository.deleteAll(oldTasks);

        taskRepository.delete(task);
        messageUtil.sendMessage(
                bundle.getPerformer(),
                task.getTitle() + " has been deleted", "The task has been deleted by "
                        + bundle.getCreator().getUser().getEmail()
        );
    }

    private Team findTeam(String teamName) {
        return teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
    }

    private Task findTaskByTitleAndBundle(String taskTitle, Bundle bundle) {
        return taskRepository.findByTitleAndBundle(taskTitle, bundle)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    private List<OldTask> getOldTasks(Task task) {
        return repository.findAllByTask(task)
                .orElseThrow(() -> new IllegalArgumentException("Wrong task title"));
    }
    private Bundle findBundle(Team team, String title) {
        return bundleRepository.findByTeamAndTitle(team, title)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
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
