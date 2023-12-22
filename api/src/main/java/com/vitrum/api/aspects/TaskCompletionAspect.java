package com.vitrum.api.aspects;

import com.vitrum.api.data.enums.StageType;
import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.submodels.TeamStage;
import com.vitrum.api.repositories.MemberRepository;
import com.vitrum.api.repositories.TeamStageRepository;
import com.vitrum.api.services.interfaces.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
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
public class TaskCompletionAspect {

    private final TeamService teamService;
    private final TeamStageRepository teamStageRepository;
    private final MemberRepository memberRepository;

    @Before("execution(* com.vitrum.api.controllers.TaskController.*(..)) && args(..)")
    public void beforeTaskModification(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        Long teamId = Long.parseLong(Objects.requireNonNull(extractTeamName(request)));
        Team team = teamService.findById(teamId);
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

        if (deadline != null && LocalDateTime.now().isAfter(deadline))
            teamService.changeStage(teamId);
    }

    private boolean checkMethod(JoinPoint joinPoint) {
        System.out.println(joinPoint.getSignature().getName());
        var methodName = joinPoint.getSignature().getName();
        return methodName.equals("confirmTask") || methodName.equals("findAll");
    }

    private String extractTeamName(HttpServletRequest request) {
        Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attribute instanceof Map<?, ?> pathVariables)
            return (String) pathVariables.get("team");
        return null;
    }
}
