package com.vitrum.api.aspects;

import com.vitrum.api.data.enums.StageType;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.submodels.TeamStage;
import com.vitrum.api.repositories.MemberRepository;
import com.vitrum.api.repositories.TeamStageRepository;
import com.vitrum.api.services.interfaces.TaskService;
import com.vitrum.api.services.interfaces.TeamService;
import com.vitrum.api.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.time.LocalDateTime;
import java.util.*;

@Aspect
@Component
@RequiredArgsConstructor
public class TaskAspect {

    private final TeamService teamService;
    private final TeamStageRepository teamStageRepository;
    private final MemberRepository memberRepository;
    private final TaskService taskService;
    private final MessageUtil messageUtil;

    @Before("execution(* com.vitrum.api.controllers.TaskController.*(..)) && args(..)")
    public void beforeTask(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        Team team = teamService.findById(Long.parseLong(Objects.requireNonNull(extractTeamId(request))));
        TeamStage teamStage = team.getCurrentStage(teamStageRepository);
        LocalDateTime deadline = teamStage.getDueDate();
        StageType current;
        try {
            current = teamStage.getType();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Stage not found");
        }

        Member currentUser = Member.getActionPerformer(memberRepository, request.getUserPrincipal(), team);

        if (currentUser.checkPermission() && current.equals(StageType.REVIEW) && !checkMethod(joinPoint))
            throw new IllegalStateException("Stage is over. Wait for the reviewing to end");

        if (deadline != null && LocalDateTime.now().isAfter(deadline) && teamStage.getNumber() != 3)
            teamService.changeStage(team.getId());
    }

    @After("execution(* com.vitrum.api.controllers.TaskController.*(..)) && args(..)")
    public void afterTask(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();
        String methodName = joinPoint.getSignature().getName();

        if (methodName.equals("findAllInReview") || methodName.equals("findAll") || methodName.equals("create"))
            return;

        Team team = teamService.findById(Long.parseLong(Objects.requireNonNull(extractTeamId(request))));
        Task task = taskService.findById(team.getId(), Long.parseLong(Objects.requireNonNull(extractTaskId(request))));

        if (methodName.equals("addPerformer"))
            messageUtil.sendMessage(
                    task.getPerformer(),
                    "TMS Info!", String.format(
                            "Team: %s\n" +
                                    "New tasks have been added to you ", task.getTeam().getName()
                    )
            );
        if (methodName.equals("update"))
            messageUtil.sendMessage(task.getPerformer(), task.getTeam().getName() + " Info!",
                    "Task has been updated: " + task.getTitle());
        if (methodName.equals("restore"))
            messageUtil.sendMessage(task.getPerformer(), task.getTeam().getName() + " Info!",
                    "The task has been restored: " + task.getTitle());

    }

    private boolean checkMethod(JoinPoint joinPoint) {
        var methodName = joinPoint.getSignature().getName();
        return methodName.equals("confirmTask") || methodName.equals("findAll");
    }

    private String extractTeamId(HttpServletRequest request) {
        Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attribute instanceof Map<?, ?> pathVariables)
            return (String) pathVariables.get("team");
        return null;
    }

    private String extractTaskId(HttpServletRequest request) {
        Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attribute instanceof Map<?, ?> pathVariables)
            return (String) pathVariables.get("task");
        return null;
    }
}
