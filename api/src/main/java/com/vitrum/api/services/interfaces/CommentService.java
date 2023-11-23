package com.vitrum.api.services.interfaces;

import java.util.Map;

public interface CommentService {

    void create(String teamName, String bundleTitle, String taskTitle, Map<String, String> request);
}
