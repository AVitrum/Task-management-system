package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.request.TaskRequest;

import java.security.Principal;

public interface TaskService {

    void add(TaskRequest request, Principal connectedUser, String team, String bundle);
    void change(TaskRequest request, String taskTitle, Principal connectedUser, String team, String bundle);
    void delete(String task, Principal connectedUser, String team, String bundle);

    Task getTask(String task, Principal connectedUser, String team, String bundle);
}
