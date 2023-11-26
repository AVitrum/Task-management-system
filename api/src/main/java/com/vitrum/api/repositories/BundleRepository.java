package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.models.Team;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BundleRepository extends MongoRepository<Bundle, String> {

    Optional<Bundle> findByCreatorAndPerformer(Member creator, Member performer);
    Optional<Bundle> findByCreatorAndTitle(Member creator, String title);
    Optional<Bundle> findByPerformerAndTitle(Member performer, String title);
    Optional<Bundle> findByTeamAndTitle(Team team, String bundleTitle);
    Optional<Bundle> findByPerformer(Member performer);

    Boolean existsByCreatorAndTitle(Member creator, String title);
    Boolean existsByPerformer(Member performer);
    Boolean existsByPerformerAndTitle(Member performer, String title);
    Boolean existsByCreatorAndPerformer(Member creator, Member performer);
}
