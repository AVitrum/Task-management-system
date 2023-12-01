package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.response.BundleResponse;

import java.security.Principal;

public interface BundleService {

    void create(String teamName, Principal connectedUser, String title);
    void addPerformer(String teamName, String bundleTitle, Principal connectedUser, String performerName);
    BundleResponse findByUser(String teamName, String bundleTitle, Principal connectedUser);

}
