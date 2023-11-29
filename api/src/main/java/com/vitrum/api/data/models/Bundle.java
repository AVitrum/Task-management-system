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

    public static Bundle findBundleByCreator(Member creator, String title) {
        for (var bundle : creator.getCreatorBundles())
            if (bundle != null && bundle.getTitle().equals(title))
                return bundle;
        throw new IllegalArgumentException("Bundle not found");
    }

    public static Bundle findBundleByTeam(Team team, String title) {
        for (var bundle : team.getBundles())
            if (bundle != null && bundle.getTitle().equals(title))
                return bundle;
        throw new IllegalArgumentException("Bundle not found");
    }

    public static Bundle findBundleByPerformer(Member performer, String title) {
        for (var bundle : performer.getPerformerBundles())
            if (bundle != null && bundle.getTitle().equals(title))
                return bundle;
        throw new IllegalArgumentException("Bundle not found");
    }
}
