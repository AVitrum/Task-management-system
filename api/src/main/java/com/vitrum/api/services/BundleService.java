package com.vitrum.api.services;

import com.vitrum.api.dto.Response.BundleResponse;

public interface BundleService {

    void create(String teamName, String title);
    void addPerformer(String teamName, String bundleTitle, String performerName);
    BundleResponse findByUser(String teamName, String bundleTitle);

}
