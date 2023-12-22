package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.response.HistoryResponse;
import com.vitrum.api.data.submodels.OldTask;

import java.security.Principal;
import java.util.List;

public interface OldTaskService {

    List<HistoryResponse> findAllByTitle(Long task, Long team, Principal connectedUser);
    OldTask getByVersion(Long task, Long team, Long version, Principal connectedUser);
    void delete(Long task, Long team, Principal connectedUser);
}
