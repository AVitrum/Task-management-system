package com.vitrum.api.data.models;

import com.vitrum.api.repositories.TeamRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "teams")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    private String id;

    private String name;

    @DBRef
    private List<Bundle> bundles;

    @DBRef
    private List<Member> members;

    public static Team findTeamByName(String teamName, TeamRepository teamRepository) {
        return teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
    }
}
