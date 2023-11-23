package com.vitrum.api.services.implementation;

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
    private final MemberRepository memberRepository;
    private final BundleRepository bundleRepository;
    private final TaskRepository taskRepository;
    private final MessageUtil messageUtil;

    @Override
    public void create(String teamName, String bundleTitle, String taskTitle, Map<String, String> request) {
        var team = findTeamByName(teamName);
        var author = findAuthor(team);
        var bundle = findBundleByTeamAndTitle(team, bundleTitle);
        var task = findTaskByTitleAndBundle(taskTitle, bundle);

        Comment comment = createComment(task, author, request);
        repository.save(comment);

        messageUtil.sendMessage(bundle.getCreator(), "Your task was commented on by", "TMS Info");
    }

    private Comment createComment(Task task, Member author, Map<String, String> request) {
        return Comment.builder()
                .author(author)
                .text(request.get("text"))
                .creationTime(LocalDateTime.now())
                .task(task)
                .build();
    }

    private Task findTaskByTitleAndBundle(String taskTitle, Bundle bundle) {
        return taskRepository.findByTitleAndBundle(taskTitle, bundle)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    private Bundle findBundleByTeamAndTitle(Team team, String bundleTitle) {
        return bundleRepository.findByTeamAndTitle(team, bundleTitle)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
    }

    private Team findTeamByName(String teamName) {
        return teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
    }

    public Member findAuthor(Team team) {
        return memberRepository.findByUserAndTeam(
                User.getAuthUser(userRepository),
                team
        ).orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

}
