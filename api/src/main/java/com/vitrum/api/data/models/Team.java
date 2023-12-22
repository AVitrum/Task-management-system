package com.vitrum.api.data.models;

import com.vitrum.api.data.submodels.TeamStage;
import com.vitrum.api.repositories.TeamRepository;
import com.vitrum.api.repositories.TeamStageRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "team")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members;

    @OneToMany(mappedBy = "team")
    private List<Task> tasks;

    @OneToMany(mappedBy = "team")
    private List<TeamStage> stages;

    public static Team findTeamById(TeamRepository teamRepository, Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
    }

    public TeamStage getCurrentStage(TeamStageRepository teamStageRepository) {
        return teamStageRepository.findByTeamAndIsCurrent(this, true).orElseThrow(
                () -> new IllegalStateException("Current stage not found"));
    }
}
