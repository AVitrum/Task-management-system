package com.vitrum.api.manager.task.history;

import com.vitrum.api.credentials.user.UserRepository;
import com.vitrum.api.dto.Response.HistoryResponse;
import com.vitrum.api.manager.member.MemberRepository;
import com.vitrum.api.manager.task.main.TaskRepository;
import com.vitrum.api.manager.task.main.TaskService;
import com.vitrum.api.manager.team.TeamRepository;
import com.vitrum.api.util.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OldTaskService {

    private final OldTaskRepository repository;
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TaskService taskService;
    private final Converter converter;

    public List<HistoryResponse> findAllByTitle(String taskTitle, String creatorName, String teamName) {
        List<OldTask> oldTasks = getOldTasks(taskTitle, creatorName, teamName);
        return oldTasks.stream().map(converter::mapOldTaskToHistoryResponse)
                .collect(Collectors.toList());
    }

    public void delete(String taskTitle, String creatorName, String teamName) {
        List<OldTask> oldTasks = getOldTasks(taskTitle, creatorName, teamName);
        repository.deleteAll(oldTasks);
        var task = taskService.getTask(taskTitle, creatorName, teamName);
        taskRepository.delete(task);
    }

    private List<OldTask> getOldTasks(String taskTitle, String creatorName, String teamName) {
        var team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        var user = userRepository.findByUsername(creatorName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var member = memberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        return repository.findAllByTitleAndMember(taskTitle, member)
                .orElseThrow(() -> new IllegalArgumentException("Wrong member or title"));
    }
}
