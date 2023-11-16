package com.vitrum.api.services;

import com.vitrum.api.dto.Request.TaskRequest;
import com.vitrum.api.models.Task;

import java.security.Principal;

public interface TaskService {

    void create(TaskRequest request, Principal connectedUser, String teamName, String bundleName);
    void change(TaskRequest request, String taskTitle, Principal connectedUser, String teamName,String bundleName);
    void delete(String taskTitle, Principal connectedUser, String teamName, String bundleName);
    Task getTask(String taskTitle, String creatorName, String teamName, String bundleName);
}
