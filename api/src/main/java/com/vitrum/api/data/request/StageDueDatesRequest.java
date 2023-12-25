package com.vitrum.api.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageDueDatesRequest {

    private String requirementsDueDate;
    private String implementationDueDate;
    private String reviewDueDate;
}
