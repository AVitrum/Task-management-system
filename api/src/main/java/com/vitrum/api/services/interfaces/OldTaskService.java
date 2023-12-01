package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.response.HistoryResponse;
import com.vitrum.api.data.submodels.OldTask;

import java.util.List;

public interface OldTaskService {

    List<HistoryResponse> findAllByTitle(String taskTitle, String teamName, String bundleTitle);
    OldTask getByVersion(
            String taskTitle,
            String teamName,
            String bundleTitle,
            Long version
    );
    void restore(
            String taskTitle,
            String teamName,
            String bundleTitle,
            Long version
    );
    void delete(String taskTitle, String teamName, String bundleTitle);
}
