package com.vitrum.api.data.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "bundles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bundle {

    @Id
    private String id;

    private String title;

    @DBRef
    private Team team;

    @DBRef
    private Member creator;

    @DBRef
    private Member performer;

    @DBRef
    private List<Task> tasks;
}
