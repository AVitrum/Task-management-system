package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.request.TaskRequest;
import com.vitrum.api.data.models.Task;

import java.security.Principal;

public interface TaskService {

    void create(TaskRequest request, Principal connectedUser, String teamName, String bundleName);
    void change(TaskRequest request, String taskTitle, Principal connectedUser, String teamName,String bundleName);
    void delete(String taskTitle, Principal connectedUser, String teamName, String bundleName);
}
