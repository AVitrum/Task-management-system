package com.vitrum.api.util;

import com.vitrum.api.data.models.User;
import com.vitrum.api.data.response.*;
import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.submodels.OldTask;
import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.models.Team;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Converter {

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
        List<Member> members = team.getMembers();
        return members.stream()
                .map(this::mapMemberToMemberResponse)
                .collect(Collectors.toList());
    }

    public MemberResponse mapMemberToMemberResponse(Member membership) {
        return MemberResponse.builder()
                .id(membership.getId())
                .name(membership.getUser().getTrueUsername())
                .role(membership.getRole())
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
        return BundleResponse.builder()
                .title(bundle.getTitle())
                .creatorEmail(bundle.getCreator().getUser().getTrueUsername())
                .performerEmail(bundle.getPerformer().getUser().getTrueUsername())
                .tasks(bundle.getTasks().stream().map(this::mapTaskToTaskResponse).collect(Collectors.toList()))
                .build();
    }
}
