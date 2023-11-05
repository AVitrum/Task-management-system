package com.vitrum.api.manager.task.main;

import com.vitrum.api.credentials.user.User;
import com.vitrum.api.credentials.user.UserRepository;
import com.vitrum.api.dto.Request.TaskRequest;
import com.vitrum.api.manager.member.Member;
import com.vitrum.api.manager.member.MemberRepository;
import com.vitrum.api.manager.task.history.OldTask;
import com.vitrum.api.manager.task.history.OldTaskRepository;
import com.vitrum.api.manager.team.TeamRepository;
import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final OldTaskRepository oldTaskRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final Converter converter;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void create(TaskRequest request, Principal connectedUser, String teamName) {
        var creator = findCreator(connectedUser, teamName);

        if (repository.findByTitleAndCreator(request.getTitle(), creator).isPresent()) {
            throw new IllegalArgumentException("A task with that name already exists in this team");
        }

        var task = createTask(request, creator);
        repository.save(task);
    }

    public void changePerformer(Map<String, String> request, String taskTitle, Principal connectedUser, String teamName) {
        var creator = findCreator(connectedUser, teamName);
        var performer = findPerformer(request.get("performer"), teamName);
        var task = findTaskByTitleAndCreator(taskTitle, creator);

        task.setPerformer(performer);
        repository.save(task);
    }

    public void change(TaskRequest request, String taskTitle, Principal connectedUser, String teamName) {
        var creator = findCreator(connectedUser, teamName);
        var task = findTaskByTitleAndCreator(taskTitle, creator);

        if (task.getStatus() == Status.DELETED) {
            throw new IllegalArgumentException("The task is not available for modification as it has been deleted");
        }

        OldTask oldTask = converter.mapTaskToOldTask(task);
        oldTaskRepository.save(oldTask);

        updateTaskFields(request, task);
        repository.save(task);
    }

    public void delete(String taskTitle, Principal connectedUser, String teamName) {
        var creator = findCreator(connectedUser, teamName);
        var task = findTaskByTitleAndCreator(taskTitle, creator);

        if (task.getStatus() == Status.DELETED) {
            throw new IllegalArgumentException("The task has already been deleted and cannot be deleted again");
        }

        task.setStatus(Status.DELETED);
        task.setVersion(task.getVersion());
        repository.save(task);

        var oldTask = converter.mapTaskToOldTask(task);
        oldTaskRepository.save(oldTask);
    }

    public Task getTask(String taskTitle, String creatorName, String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var user = userRepository.findByUsername(creatorName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var member = memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

        return repository.findByTitleAndCreator(taskTitle, member)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    private Member findMemberByUsernameAndTeam(String username, String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
    }

    private Member findCreator(Principal connectedUser, String teamName) {
        var user = User.getUserFromPrincipal(connectedUser);
        return findMemberByUsernameAndTeam(user.getTrueUsername(), teamName);
    }

    private Member findPerformer(String performer, String teamName) {
        return findMemberByUsernameAndTeam(performer, teamName);
    }


    private Task createTask(TaskRequest request, Member creator) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .version(0L)
                .creationTime(LocalDateTime.now())
                .status(Status.PENDING)
                .dueDate(LocalDateTime.parse(request.getDueDate(), formatter))
                .creator(creator)
                .performer(creator)
                .build();
    }

    private Task findTaskByTitleAndCreator(String taskTitle, Member creator) {
        return repository.findByTitleAndCreator(taskTitle, creator)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    private void updateTaskFields(TaskRequest request, Task task) {
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(LocalDateTime.parse(request.getDueDate(), formatter));
        }
        if (request.getStatus() != null) {
            task.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
        }
        task.setVersion(task.getVersion() + 1);
    }
}
