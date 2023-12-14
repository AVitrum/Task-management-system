package com.vitrum.api.aspects;

import com.vitrum.api.services.interfaces.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.time.LocalDateTime;
import java.util.*;

@Aspect
@Component
@RequiredArgsConstructor
public class TaskCompletionAspect {

    private final TaskService taskService;
    private final Set<String> MODIFICATION_METHODS = new HashSet<>(Arrays.asList("POST", "PATCH", "DELETE"));

    @Before("execution(* com.vitrum.api.controllers.TaskController.*(..)) && args(..)")
    public void beforeTaskModification() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        if (shouldCheckDeadline(request)) {
            String method = request.getMethod();
            if (MODIFICATION_METHODS.contains(method) && isChangingRequest()) {
                LocalDateTime deadline = taskService.getDeadlineForTasks(extractTeamName(request));

                if (deadline != null && LocalDateTime.now().isAfter(deadline))
                    throw new IllegalStateException("The stage is over, wait for the new one to start");
            }
        }
    }

    private boolean shouldCheckDeadline(HttpServletRequest request) {
        return RequestMethod.valueOf(request.getMethod()) != RequestMethod.GET;
    }

    private boolean isChangingRequest() {
        HandlerMethod handlerMethod = (HandlerMethod) RequestContextHolder.currentRequestAttributes()
                .getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        return Objects.requireNonNull(handlerMethod).getMethod().isAnnotationPresent(PostMapping.class)
                || handlerMethod.getMethod().isAnnotationPresent(PutMapping.class)
                || handlerMethod.getMethod().isAnnotationPresent(PatchMapping.class)
                || handlerMethod.getMethod().isAnnotationPresent(DeleteMapping.class);
    }

    private String extractTeamName(HttpServletRequest request) {
        Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attribute instanceof Map<?, ?> pathVariables) {
            return (String) pathVariables.get("team");
        }
        return null;
    }
}
