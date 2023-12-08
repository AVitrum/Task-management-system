package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.request.BundleRequest;
import com.vitrum.api.data.response.BundleResponse;

import java.security.Principal;
import java.util.List;

public interface BundleService {

    void create(String teamName, Principal connectedUser, BundleRequest request);
    void addPerformer(String teamName, String bundleTitle, Principal connectedUser, String performerName);
    void deleteByTitle(String team, String bundle, Principal connectedUser);
    Bundle findByTitle(String teamName, String bundleTitle, Principal connectedUser);
    List<BundleResponse> findAll(String team, Principal connectedUser);

}
