package com.vitrum.api.services;

import com.vitrum.api.dto.Response.BundleResponse;

import java.security.Principal;

public interface BundleService {

    void create(String teamName, Principal connectedUser, String title);
    void addPerformer(String teamName, String bundleTitle, Principal connectedUser, String performerName);
    BundleResponse findByUser(String teamName, String bundleTitle, Principal connectedUser);

}
