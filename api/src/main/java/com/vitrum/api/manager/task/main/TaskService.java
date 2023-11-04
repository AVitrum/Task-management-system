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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final OldTaskRepository oldTaskRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final Converter converter;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void create(TaskRequest request, Principal connectedUser, String teamName) {
        try {
            var member = findMember(connectedUser, teamName);

            if (repository.findByTitleAndCreator(request.getTitle(), member).isPresent())
                throw new IllegalArgumentException("A task with that name already exists in this team");

            var task = Task.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .priority(request.getPriority())
                    .version(0L)
                    .creationTime(LocalDateTime.now())
                    .status(Status.PENDING)
                    .dueDate(LocalDateTime.parse(request.getDueDate(), formatter))
                    .build();
            task.setCreator(member);
            repository.save(task);

        } catch (IllegalStateException e) {
            throw new IllegalStateException("Can't create");
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Title is a required field");
        }
    }

    public void change(TaskRequest request, String taskTitle, Principal connectedUser, String teamName) {
        try {
            var member = findMember(connectedUser, teamName);
            var task = repository.findByTitleAndCreator(taskTitle, member)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found"));

            if (task.getStatus() == Status.DELETED)
                throw new IllegalArgumentException(
                        "The task is not available for modification as it has been deleted"
                );

            OldTask oldTask = converter.mapTaskToOldTask(task);
            oldTaskRepository.save(oldTask);

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
            repository.save(task);

        } catch (IllegalStateException e) {
            throw new IllegalStateException("Can't change");
        }
    }

    public void delete(String taskTitle, Principal connectedUser, String teamName) {
        try {
            var member = findMember(connectedUser, teamName);
            var task = repository.findByTitleAndCreator(taskTitle, member)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found"));

            if (task.getStatus() == Status.DELETED)
                throw new IllegalArgumentException(
                        "The task has already been deleted and cannot be deleted again"
                );

            task.setStatus(Status.DELETED);
            task.setVersion(task.getVersion());
            repository.save(task);

            var oldTask = converter.mapTaskToOldTask(task);
            oldTaskRepository.save(oldTask);

        } catch (IllegalStateException e) {
            throw new IllegalStateException("Can't delete");
        }
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

    private Member findMember(Principal connectedUser, String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        return memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
    }
}
