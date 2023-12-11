package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.request.TaskRequest;

import java.security.Principal;

public interface TaskService {

    void add(TaskRequest request, Principal connectedUser, String team, String bundle);
    void change(TaskRequest request, String taskTitle, String team, String bundle, Principal connectedUser);
    void delete(String task, Principal connectedUser, String team, String bundle);

    void markAsCompleted(
            String taskTitle,
            String teamName,
            String bundleTitle,
            Principal connectedUser
    );

    Task getTask(String task, Principal connectedUser, String team, String bundle);
}
