package com.vitrum.api.services.interfaces;

import com.vitrum.api.data.response.BundleResponse;

import java.util.List;

public interface BundleService {

    void create(String teamName, String title);
    void addPerformer(String teamName, String bundleTitle, String performerName);
    void deleteByTitle(String teamName, String bundleTitle);
    BundleResponse findByUser(String teamName);
    BundleResponse findByTitle(String team, String bundle);
    List<BundleResponse> findAll(String team);

}
