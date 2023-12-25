package com.vitrum.api.services.implementations;

import com.amazonaws.services.s3.AmazonS3;
import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.models.*;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.*;
import com.vitrum.api.data.response.HistoryResponse;
import com.vitrum.api.services.interfaces.OldTaskService;
import com.vitrum.api.util.Converter;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OldTaskServiceImpl implements OldTaskService {

    private final OldTaskRepository repository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final MessageUtil messageUtil;
    private final AmazonS3 s3Client;
    private final Converter converter;

    @Override
    public List<HistoryResponse> findAllByTitle(
            Long taskId,
            Long teamId,
            Principal connectedUser
    ) {
        Task task = Task.findTask(taskRepository, Team.findTeamById(teamRepository, teamId), taskId);

        List<OldTask> oldTasks = getOldTasks(task);

        return oldTasks.stream()
                .map(converter::mapOldTaskToHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OldTask getByVersion(
            Long taskId,
            Long teamId,
            Long version,
            Principal connectedUser
    ) {
        Task task = Task.findTask(taskRepository, Team.findTeamById(teamRepository, teamId), taskId);

        return repository.findByTaskAndVersion(task, version)
                .orElseThrow(() -> new IllegalArgumentException("Task version not found"));
    }

    @Override
    public void delete(Long taskId, Long teamId, Principal connectedUser) {
        Task task = Task.findTask(taskRepository, Team.findTeamById(teamRepository, teamId), taskId);

        if (task.getStatus().equals(Status.COMPLETED))
            throw new IllegalStateException("You cannot delete tasks that have already been completed");

        checkPermission(connectedUser, task);
        task.delete(taskRepository, commentRepository, repository, fileRepository, s3Client);

        messageUtil.sendMessage(
                task.getPerformer(),
                task.getTitle() + " has been deleted", "The task has been deleted by "
                        + task.getCreator().getUser().getEmail()
        );
    }

    private void checkPermission(Principal connectedUser, Task task) {
        Member actionPerformer = Member.getActionPerformer(memberRepository, connectedUser, task.getTeam());

        if (!actionPerformer.equals(task.getCreator())
                && actionPerformer.checkPermission()
        ) throw new IllegalStateException("You do not have permission for this action");
    }

    private List<OldTask> getOldTasks(Task task) {
        return repository.findAllByTask(task)
                .orElseThrow(() -> new IllegalArgumentException("Wrong task title"));
    }
}
