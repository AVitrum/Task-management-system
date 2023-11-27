package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.request.TaskRequest;
import com.vitrum.api.data.models.Task;

public interface TaskService {

    void create(TaskRequest request, String teamName, String bundleName);
    void change(TaskRequest request, String taskTitle, String teamName,String bundleName);
    void delete(String taskTitle, String teamName, String bundleName);
    Task getTask(String taskTitle, String creatorName, String teamName, String bundleName);
}
