package com.vitrum.api.manager.task.history;

import com.vitrum.api.credentials.user.UserRepository;
import com.vitrum.api.dto.Response.HistoryResponse;
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
    private final TaskService taskService;
    private final Converter converter;

    public List<HistoryResponse> findAllByTitle(String taskTitle, String creatorName, String teamName) {
        List<OldTask> oldTasks = getOldTasks(taskTitle, creatorName, teamName);
        return oldTasks.stream()
                .map(converter::mapOldTaskToHistoryResponse)
                .collect(Collectors.toList());
    }

    public OldTask getByVersion(String taskTitle, String creatorName, String teamName, Long version) {
        return repository.findByTitleAndCreatorAndVersion(taskTitle, findMember(creatorName, teamName), version)
                .orElseThrow(() -> new IllegalArgumentException("Task version not found"));
    }

    public void restore(String taskTitle, String creatorName, String teamName, Long version) {
        OldTask oldTask = getByVersion(taskTitle, creatorName, teamName, version);
        Task task = taskRepository.findByTitleAndCreator(oldTask.getTitle(), oldTask.getCreator())
                .orElseThrow(() -> new IllegalArgumentException("Task or Task version not found"));

        List<OldTask> oldTasks = getOldTasks(taskTitle, creatorName, teamName);
        repository.deleteAll(oldTasks.subList(version.intValue(), oldTasks.size()));

        updateTaskFields(oldTask, task);

        task.setStatus(Status.RESTORED);
        taskRepository.save(task);
    }

    public void delete(String taskTitle, String creatorName, String teamName) {
        List<OldTask> oldTasks = getOldTasks(taskTitle, creatorName, teamName);
        repository.deleteAll(oldTasks);

        taskRepository.delete(taskService.getTask(taskTitle, creatorName, teamName));
    }

    private List<OldTask> getOldTasks(String taskTitle, String creatorName, String teamName) {
        return repository.findAllByTitleAndCreator(taskTitle, findMember(creatorName, teamName))
                .orElseThrow(() -> new IllegalArgumentException("Wrong member or title"));
    }

    private Member findMember(String creatorName, String teamName) {
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
        task.setCreator(oldTask.getCreator());
        task.setStatus(oldTask.getStatus());
    }
}
