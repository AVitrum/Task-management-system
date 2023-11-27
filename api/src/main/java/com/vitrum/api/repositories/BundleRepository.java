package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.models.Team;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BundleRepository extends MongoRepository<Bundle, String> {

    Optional<Bundle> findByCreatorAndPerformer(Member creator, Member performer);
    Optional<Bundle> findByCreatorAndTitle(Member creator, String title);
    Optional<Bundle> findByTeamAndTitle(Team team, String bundleTitle);
    Optional<Bundle> findByPerformer(Member performer);
    Optional<Bundle> findByCreator(Member creator);

    List<Bundle> findAllByTeam(Team team);

    Boolean existsByCreatorAndTitle(Member creator, String title);
    Boolean existsByCreator(Member creator);
    Boolean existsByPerformer(Member performer);
    Boolean existsByCreatorAndPerformer(Member creator, Member performer);
}
