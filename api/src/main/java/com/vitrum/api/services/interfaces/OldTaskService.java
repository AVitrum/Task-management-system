package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.response.HistoryResponse;
import com.vitrum.api.data.submodels.OldTask;

import java.security.Principal;
import java.util.List;

public interface OldTaskService {

    List<HistoryResponse> findAllByTitle(String task, String team, String bundle, Principal connectedUser);
    OldTask getByVersion(
            String task,
            String team,
            String bundle,
            Long version,
            Principal connectedUser);
    void restore(
            String task,
            String team,
            String bundle,
            Long version,
            Principal connectedUser);
    void delete(String task, String team, String bundle, Principal connectedUser);
}
