package com.vitrum.api.services;

import com.vitrum.api.dto.Response.HistoryResponse;
import com.vitrum.api.models.submodels.OldTask;

import java.util.List;

public interface OldTaskService {

    List<HistoryResponse> findAllByTitle(String taskTitle, String creatorName, String teamName, String bundleName);
    OldTask getByVersion(
            String taskTitle,
            String creatorName,
            String teamName,
            String bundleName,
            Long version
    );
    void restore(
            String taskTitle,
            String creatorName,
            String teamName,
            String bundleName,
            Long version
    );
    void delete(String taskTitle, String creatorName, String teamName, String bundleName);
}
