package com.vitrum.api.task;

import com.vitrum.api.credentials.user.User;
import com.vitrum.api.dto.Request.TaskCreationRequest;
import com.vitrum.api.member.MemberRepository;
import com.vitrum.api.team.TeamRepository;
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
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public void createTask(TaskCreationRequest request, Principal connectedUser, String teamName) {
        try {
            var team = teamRepository.findByName(teamName)
                    .orElseThrow(() -> new IllegalArgumentException("Team not found"));
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            var member = memberRepository.findByUserAndTeam(user, team)
                    .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            var task = Task.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .priority(request.getPriority())
                    .creationTime(LocalDateTime.now())
                    .dueDate(LocalDateTime.parse(request.getDueDate(), formatter))
                    .build();
            task.setMember(member);
            repository.save(task);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Can't create");
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Title is a required field");
        }
    }
}
