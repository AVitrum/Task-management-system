package com.vitrum.api.data.models;

import com.vitrum.api.data.submodels.TeamStage;
import com.vitrum.api.repositories.TeamRepository;
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

    @OneToOne(mappedBy = "team")
    private TeamStage currentStage;

    public static Team findTeamByName(TeamRepository teamRepository, String name) {
        return teamRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
    }
}
