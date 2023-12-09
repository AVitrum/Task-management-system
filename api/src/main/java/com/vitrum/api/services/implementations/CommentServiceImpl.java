package com.vitrum.api.services.implementations;

import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.models.Comment;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.repositories.*;
import com.vitrum.api.services.interfaces.CommentService;
import com.vitrum.api.services.interfaces.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final MemberRepository memberRepository;
    private final TaskService taskService;

    @Override
    public void add(Map<String, String> request, Principal connectedUser, String teamName, String bundleTitle, String taskTitle) {
        var task = taskService.getTask(taskTitle, connectedUser, teamName, bundleTitle);


        if (task.getStatus().equals(Status.DELETED))
            throw new IllegalStateException("Task was deleted");

        repository.save(
                Comment.builder()
                        .author(Member.getActionPerformer(memberRepository, connectedUser, task.getBundle().getTeam()))
                        .text(request.get("text"))
                        .creationTime(LocalDateTime.now())
                        .task(task)
                        .build()
        );


    }
}
