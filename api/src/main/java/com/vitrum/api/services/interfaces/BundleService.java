package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.response.BundleResponse;

public interface BundleService {

    void create(String teamName, String title);
    void addPerformer(String teamName, String bundleTitle, String performerName);
    BundleResponse findByUser(String teamName, String bundleTitle);

}
