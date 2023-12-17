package com.vitrum.api.aspects;

import com.vitrum.api.services.interfaces.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.time.LocalDateTime;
import java.util.*;

@Aspect
@Component
@RequiredArgsConstructor
public class TaskCompletionAspect {

    private final TaskService taskService;

    @Before("execution(* com.vitrum.api.controllers.TaskController.*(..)) && args(..)")
    public void beforeTaskModification(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        String teamName = extractTeamName(request);
        LocalDateTime deadline = taskService.getDeadlineForTasks(teamName);

        if (deadline != null && LocalDateTime.now().isAfter(deadline)) {
            taskService.markOverDueTasks(teamName);
            if (checkMethod(request, joinPoint))
                throw new IllegalStateException("The stage is over, wait for the new one to start");
        }
    }

    private boolean checkMethod(HttpServletRequest request, JoinPoint joinPoint) {
        return !joinPoint.getSignature().getName().equals("update")
                && RequestMethod.valueOf(request.getMethod()) != RequestMethod.GET;
    }

    private String extractTeamName(HttpServletRequest request) {
        Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attribute instanceof Map<?, ?> pathVariables)
            return (String) pathVariables.get("team");
        return null;
    }
}
