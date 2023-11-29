package com.vitrum.api.services.implementation;

import com.vitrum.api.data.enums.Status;
import com.vitrum.api.data.models.*;
import com.vitrum.api.data.submodels.Comment;
import com.vitrum.api.repositories.*;
import com.vitrum.api.services.interfaces.CommentService;
import com.vitrum.api.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final MessageUtil messageUtil;

    @Override
    public void create(String teamName, String bundleTitle, String taskTitle, Map<String, String> request) {
        var team = Team.findTeamByName(teamName, teamRepository);
        var author = Member.findMemberByKey(User.getUsername(userRepository), team.getMembers());
        var bundle = Bundle.findBundleByTeam(team, bundleTitle);
        var task = Task.findTaskByTitleAndBundle(bundle, taskTitle);

        if (task.getStatus().equals(Status.DELETED))
            throw new IllegalArgumentException("Task has been deleted");

        Comment comment = createComment(task, author, request);
        repository.save(comment);

        task.getComments().add(comment);
        taskRepository.save(task);

        messageUtil.sendMessage(
                bundle.getCreator(),
                "TMS Info",
                String.format("Your task - %s, was commented by %s", taskTitle, author.getUser().getTrueUsername())
        );
    }

    private Comment createComment(Task task, Member author, Map<String, String> request) {
        return Comment.builder()
                .author(author)
                .text(request.get("text"))
                .creationTime(LocalDateTime.now())
                .task(task)
                .build();
    }
}
