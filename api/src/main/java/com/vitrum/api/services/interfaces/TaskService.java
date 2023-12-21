package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.request.TaskRequest;
import com.vitrum.api.data.response.TaskResponse;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface TaskService {

    void create(String teamName, Principal connectedUser, TaskRequest request);
    void addPerformer(String teamName, String taskTitle, Principal connectedUser, String performerName);
    void update(String teamName, String taskTitle, Principal connectedUser, TaskRequest request);
    void restoreByTitle(String taskTitle, String teamName, Principal connectedUser);
    void deleteByTitle(String teamName, String taskTitle, Principal connectedUser);

    String confirmTask(String teamName, String taskTitle, Principal connectedUser);
    String changeCategory(Map<String, String> request, String teamName, String taskTitle, Principal connectedUser);

    Task findByTitle(String teamName, String taskTitle, Principal connectedUser);

    List<TaskResponse> findAll(String team, Principal connectedUser);
    List<TaskResponse> findAllInReview(String teamName, Principal coneectedUser);
}
