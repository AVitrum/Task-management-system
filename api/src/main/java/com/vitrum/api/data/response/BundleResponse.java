package com.vitrum.api.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BundleResponse {

    private String title;
    private String creatorEmail;
    private String performerEmail;
    private List<TaskResponse> tasks;
}
