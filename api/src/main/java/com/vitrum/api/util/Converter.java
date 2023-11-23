package com.vitrum.api.util;

import com.vitrum.api.data.models.*;
import com.vitrum.api.data.response.*;
import com.vitrum.api.data.submodels.Comment;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.CommentRepository;
import com.vitrum.api.repositories.MemberRepository;
import com.vitrum.api.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Converter {

    private final MemberRepository memberRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    public TeamResponse mapTeamToTeamResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .members(getMemberResponse(team))
                .build();
    }

    public UserProfileResponse mapUserToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getTrueUsername())
                .role(user.getRole())
                .build();
    }

    public List<MemberResponse> getMemberResponse(Team team) {

        List<Member> members = memberRepository.findAllByTeam(team);

        return members.stream()
                .map(this::mapMemberToMemberResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getCommentResponse(OldTask oldTask) {

        List<Comment> comments = oldTask.getComments();

        return comments.stream()
                .map(this::mapCommentToCommentResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapCommentToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .author(comment.getAuthor().getUser().getTrueUsername())
                .text(comment.getText())
                .build();
    }

    public MemberResponse mapMemberToMemberResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getUser().getTrueUsername())
                .role(member.getRole())
                .build();
    }

    public OldTask mapTaskToOldTask(Task task) {
        return OldTask.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .version(task.getVersion())
                .creationTime(task.getCreationTime())
                .dueDate(task.getDueDate())
                .changeTime(LocalDateTime.now())
                .status(task.getStatus())
                .comments(commentRepository.findAllByTask(task))
                .task(task)
                .build();
    }

    public HistoryResponse mapOldTaskToHistoryResponse(OldTask oldTask) {
        return HistoryResponse.builder()
                .taskId(oldTask.getTask().getId())
                .id(oldTask.getId())
                .version(oldTask.getVersion())
                .title(oldTask.getTitle())
                .description(oldTask.getDescription())
                .priority(oldTask.getPriority())
                .status(oldTask.getStatus().name())
                .changeTime(oldTask.getChangeTime())
                .creationTime(oldTask.getCreationTime())
                .dueDate(oldTask.getDueDate())
                .creator(mapMemberToMemberResponse(oldTask.getTask().getBundle().getCreator()))
                .comments(getCommentResponse(oldTask))
                .build();
    }

    public TaskResponse mapTaskToTaskResponse(Task task) {
        return TaskResponse.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .version(task.getVersion())
                .creationTime(task.getCreationTime())
                .dueDate(task.getDueDate())
                .status(task.getStatus().name())
                .priority(task.getPriority())
                .build();
    }

    public BundleResponse mapBundleToBundleResponse(Bundle bundle) {

        List<Task> tasks = taskRepository.findAllByBundle(bundle);

        return BundleResponse.builder()
                .title(bundle.getTitle())
                .creatorEmail(bundle.getCreator().getUser().getTrueUsername())
                .performerEmail(bundle.getPerformer().getUser().getTrueUsername())
                .tasks(tasks.stream().map(this::mapTaskToTaskResponse).collect(Collectors.toList()))
                .build();
    }
}
