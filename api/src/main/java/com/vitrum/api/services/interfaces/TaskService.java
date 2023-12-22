package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.request.TaskRequest;
import com.vitrum.api.data.response.TaskResponse;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface TaskService {

    void create(Long teamId, Principal connectedUser, TaskRequest request);
    void addPerformer(Long teamId, Long taskId, Principal connectedUser, String performerName);
    void update(Long teamId, Long taskId, Principal connectedUser, TaskRequest request);
    void restoreByTitle(Long taskId, Long teamId, Principal connectedUser);
    void deleteByTitle(Long teamId, Long taskId, Principal connectedUser);

    String confirmTask(Long teamId, Long taskId, Principal connectedUser);
    String changeCategory(Map<String, String> request, Long teamId, Long taskId, Principal connectedUser);

    Task findByTitle(Long teamId, Long taskId, Principal connectedUser);

    List<TaskResponse> findAll(Long teamId, Principal connectedUser);
    List<TaskResponse> findAllInReview(Long teamId, Principal connectedUser);
}
