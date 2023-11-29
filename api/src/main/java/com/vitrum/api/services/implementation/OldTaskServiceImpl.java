package com.vitrum.api.services.implementation;

import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.models.User;
import com.vitrum.api.data.response.HistoryResponse;
import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.*;
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
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final BundleRepository bundleRepository;
    private final CommentRepository commentRepository;
    private final MessageUtil messageUtil;
    private final Converter converter;

    @Override
    public List<HistoryResponse> findAllByTitle(
            String taskTitle,
            String teamName,
            String bundleTitle
    ) {
        var team = Team.findTeamByName(teamName, teamRepository);
        var task = Task.findTaskByTitleAndBundle(
                Bundle.findBundleByTeam(team, bundleTitle),
                taskTitle
        );

        return task.getOldTasks().stream()
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
        var task = Task.findTaskByTitleAndBundle(
                Bundle.findBundleByTeam(Team.findTeamByName(
                        teamName,
                        teamRepository
                ), bundleTitle),
                taskTitle
        );

        return OldTask.findByTaskAndVersion(task, version);
    }

    @Override
    public void restore(
            String taskTitle,
            String teamName,
            String bundleTitle,
            Long version
    ) {
        OldTask oldTask = getByVersion(taskTitle, teamName, bundleTitle, version);
        var bundle = Bundle.findBundleByTeam(
                Team.findTeamByName(
                        teamName,
                teamRepository
        ), bundleTitle);
        var task = Task.findTaskByTitleAndBundle(bundle, taskTitle);

        List<OldTask> oldTasks = task.getOldTasks().subList(version.intValue(), task.getOldTasks().size());

        oldTasks.stream().map(OldTask::getComments).forEach(commentRepository::deleteAll);

        repository.deleteAll(oldTasks);

        updateTaskFields(oldTask, task);
        task.setOldTasks(task.getOldTasks().subList(0, version.intValue()));
        taskRepository.save(task);

        messageUtil.sendMessage(bundle.getPerformer(), "The task has been restored", task.toString());
    }

    @Override
    public void delete(String taskTitle, String teamName, String bundleTitle) {
        var bundle = Bundle.findBundleByTeam(
                Team.findTeamByName(
                        teamName,
                        teamRepository
                ), bundleTitle);
        var task = Task.findTaskByTitleAndBundle(bundle, taskTitle);

        List<OldTask> oldTasks = task.getOldTasks();
        repository.deleteAll(oldTasks);

        commentRepository.deleteAll(task.getComments());

        bundle.getTasks().remove(task);
        taskRepository.delete(task);

        bundleRepository.save(bundle);

        messageUtil.sendMessage(
                bundle.getPerformer(),
                task.getTitle() + " has been deleted", "The task has been deleted by " + User.getUsername(userRepository)
        );
    }

    private void updateTaskFields(OldTask oldTask, Task task) {
        task.setTitle(oldTask.getTitle());
        task.setPriority(oldTask.getPriority());
        task.setDescription(oldTask.getDescription());
        task.setVersion(oldTask.getVersion());
        task.setDueDate(oldTask.getDueDate());
        task.setStatus(Status.RESTORED);
        task.setComments(oldTask.getComments());
    }
}
