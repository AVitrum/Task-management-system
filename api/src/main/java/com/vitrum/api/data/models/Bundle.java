package com.vitrum.api.data.models;

import com.vitrum.api.repositories.BundleRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "bundle")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Member creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performer_id")
    private Member performer;

    @OneToMany(mappedBy = "bundle")
    private List<Task> tasks;

    public static Bundle findBundle(BundleRepository bundleRepository, Team team, String title) {
        return bundleRepository.findByTeamAndTitle(team, title)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));
    }
}

