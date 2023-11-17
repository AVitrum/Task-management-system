package com.vitrum.api.services;

import com.vitrum.api.dto.Request.TaskRequest;
import com.vitrum.api.models.Task;

public interface TaskService {

    void create(TaskRequest request, String teamName, String bundleName);
    void change(TaskRequest request, String taskTitle, String teamName,String bundleName);
    void delete(String taskTitle, String teamName, String bundleName);
    Task getTask(String taskTitle, String creatorName, String teamName, String bundleName);
}
