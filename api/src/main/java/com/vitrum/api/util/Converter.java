package com.vitrum.api.util;

import com.vitrum.api.data.models.*;
import com.vitrum.api.data.response.*;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Converter {

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
                .imagePath(user.getImagePath())
                .build();
    }

    public List<MemberResponse> getMemberResponse(Team team) {
        List<Member> members = team.getMembers();
        return members.stream()
                .map(this::mapMemberToMemberResponse)
                .collect(Collectors.toList());
    }

    public MemberResponse mapMemberToMemberResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getUser().getTrueUsername())
                .role(member.getRole())
                .user(mapUserToUserProfileResponse(member.getUser()))
                .build();
    }

    public OldTask mapTaskToOldTask(Task task) {
        OldTask oldTask = OldTask.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .version(task.getVersion())
                .status(task.getStatus())
                .task(task)
                .build();

        List<Comment> comments = new ArrayList<>();
        task.getComments().forEach(comment -> {
            Comment newComment = Comment.builder()
                    .text(comment.getText())
                    .creationTime(comment.getCreationTime())
                    .author(comment.getAuthor())
                    .oldTask(oldTask)
                    .build();
            comments.add(newComment);
        });

        commentRepository.deleteAll(task.getComments());
        oldTask.setComments(comments);
        return oldTask;
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
                .comments(oldTask.getComments().stream()
                        .map(this::mapCommentToCommentResponse)
                        .collect(Collectors.toList()))
                .creator(mapMemberToMemberResponse(oldTask.getTask().getBundle().getCreator()))
                .build();
    }

    public CommentResponse mapCommentToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .author(comment.getAuthor().getUser().getTrueUsername())
                .text(comment.getText())
                .creationTime(comment.getCreationTime())
                .build();
    }

    public TaskResponse mapTaskToTaskResponse(Task task) {
        return TaskResponse.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .version(task.getVersion())
                .status(task.getStatus().name())
                .priority(task.getPriority())
                .comments(task.getComments().stream()
                        .map(this::mapCommentToCommentResponse)
                        .collect(Collectors.toList()))
                .files(task.getFiles().stream()
                        .map(File::getPath)
                        .collect(Collectors.toList()))
                .build();
    }

    public BundleResponse mapBundleToBundleResponse(Bundle bundle) {
        return BundleResponse.builder()
                .title(bundle.getTitle())
                .creatorEmail(bundle.getCreator().getUser().getTrueUsername())
                .performerEmail(bundle.getPerformer().getUser().getTrueUsername())
                .assignmentDate(bundle.getAssignmentDate())
                .changeTime(bundle.getChangeTime())
                .dueDate(bundle.getDueDate())
                .tasks(bundle.getTasks().stream()
                        .map(this::mapTaskToTaskResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
