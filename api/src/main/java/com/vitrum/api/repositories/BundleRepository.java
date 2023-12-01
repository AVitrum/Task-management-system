package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BundleRepository extends JpaRepository<Bundle, Long> {

    Optional<Bundle> findByCreatorAndPerformer(Member creator, Member performer);
    Optional<Bundle> findByCreatorAndTitle(Member creator, String title);
    Optional<Bundle> findByPerformer(Member performer);
    Optional<Bundle> findByPerformerAndTitle(Member performer, String title);
    Optional<Bundle> findByTeamAndTitle(Team team, String title);

    Boolean existsByCreatorAndTitle(Member creator, String title);
    Boolean existsByPerformerAndTitle(Member performer, String title);
    Boolean existsByCreatorAndPerformer(Member creator, Member performer);

    Boolean existsByPerformer(Member performer);

}
