package com.vitrum.api.util;

import com.vitrum.api.data.models.*;
import com.vitrum.api.data.response.*;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.repositories.TeamStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Converter {

    private final TeamStageRepository teamStageRepository;

    public TeamResponse mapTeamToTeamResponse(Team team) {

        String stage = null;
        LocalDateTime dueDate = null;

        if (teamStageRepository.existsByTeamAndIsCurrent(team, true)) {
            stage = team.getCurrentStage(teamStageRepository).getType().name();
            dueDate = team.getCurrentStage(teamStageRepository).getDueDate();
        }

        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .members(getMemberResponse(team))
                .stage(stage)
                .stageDueDate(dueDate)
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


    public CommentResponse mapCommentToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .author(comment.getAuthor().getUser().getTrueUsername())
                .text(comment.getText())
                .creationTime(comment.getCreationTime())
                .build();
    }

    public TaskResponse mapTaskToTaskResponse(Task task) {
        List<String> categories = task.getCategories().stream().map(Enum::name).toList();

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .status(task.getStatus().name())
                .isCompleted(task.getCompleted())
                .description(task.getDescription())
                .assignmentDate(task.getAssignmentDate())
                .changeTime(task.getChangeTime())
                .creator(mapMemberToMemberResponse(task.getCreator()))
                .performer(mapMemberToMemberResponse(task.getPerformer()))
                .categories(categories)
                .comments(task.getComments().stream().map(this::mapCommentToCommentResponse).toList())
                .build();
    }

    public OldTask mapTaskToOldTask(Task task) {
        return OldTask.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .version(task.getVersion())
                .status(task.getStatus())
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
                .status(oldTask.getStatus().name())
                .build();
    }
}
