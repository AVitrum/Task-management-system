package com.vitrum.api.services.implementation;

import com.vitrum.api.data.models.*;
import com.vitrum.api.data.request.TaskRequest;
import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.*;
import com.vitrum.api.services.interfaces.TaskService;
import com.vitrum.api.util.Converter;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final OldTaskRepository oldTaskRepository;
    private final TeamRepository teamRepository;
    private final BundleRepository bundleRepository;
    private final UserRepository userRepository;
    private final Converter converter;
    private final MessageUtil messageUtil;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void create(TaskRequest request, String teamName, String bundleTitle) {
        var team = Team.findTeamByName(teamName, teamRepository);
        var creator = Member.findCreator(team, userRepository);

        if (creator.checkPermissionToCreate())
            throw new IllegalArgumentException("You do not have permission to perform this action");

        var bundle = Bundle.findBundleByTeam(team, bundleTitle);

        if (repository.existsByTitleAndBundle(request.getTitle(), bundle))
            throw new IllegalArgumentException("A task with that name already exists in this team");

        Task task = createTask(request, bundle);
        repository.save(task);

        bundle.getTasks().add(task);
        bundleRepository.save(bundle);
    }

    @Override
    public void change(TaskRequest request, String taskTitle, String teamName,String bundleTitle) {
        var team = Team.findTeamByName(teamName, teamRepository);
        var creator = Member.findCreator(team, userRepository);
        var bundle = Bundle.findBundleByTeam(team, bundleTitle);
        var task = Task.findTaskByTitleAndBundle(bundle, taskTitle);

        if (task.getStatus() == Status.DELETED)
            throw new IllegalArgumentException("The task is not available for modification as it has been deleted");

        OldTask oldTask = converter.mapTaskToOldTask(task);
        oldTaskRepository.save(oldTask);

        updateTaskFields(request, task, oldTask);
        repository.save(task);

        messageUtil.sendMessage(
                bundle.getPerformer(),
                String.format("The task has been changed by %s", creator.getUser().getEmail()),
                task.toString()
        );
    }

    @Override
    public void delete(String taskTitle, String teamName, String bundleTitle) {
        var team = Team.findTeamByName(teamName, teamRepository);
        var creator = Member.findCreator(team, userRepository);
        var bundle = Bundle.findBundleByCreator(creator, bundleTitle);
        var task = Task.findTaskByTitleAndBundle(bundle, taskTitle);

        if (task.getStatus() == Status.DELETED)
            throw new IllegalArgumentException("The task has already been deleted and cannot be deleted again");

        task.setStatus(Status.DELETED);
        task.setVersion(task.getVersion());

        var oldTask = converter.mapTaskToOldTask(task);
        oldTaskRepository.save(oldTask);

        task.getOldTasks().add(oldTask);
        repository.save(task);

        messageUtil.sendMessage(
                task.getBundle().getPerformer(),
                String.format("The task has been deleted by %s", creator.getUser().getEmail()),
                task.toString()
        );
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
                .oldTasks(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();
    }

    private void updateTaskFields(TaskRequest request, Task task, OldTask oldTask) {
        if (request.getDescription() != null)
            task.setDescription(request.getDescription());

        if (request.getPriority() != null)
            task.setPriority(request.getPriority());

        if (request.getDueDate() != null)
            task.setDueDate(LocalDateTime.parse(request.getDueDate(), formatter));

        if (request.getStatus() != null)
            task.setStatus(Status.valueOf(request.getStatus().toUpperCase()));

        task.setVersion(task.getVersion() + 1);
        task.getOldTasks().add(oldTask);
        task.setComments(new ArrayList<>());
    }
}
