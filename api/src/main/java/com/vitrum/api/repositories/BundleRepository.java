package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Member;
import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BundleRepository extends JpaRepository<Bundle, Long> {

    Optional<Bundle> findByPerformer(Member performer);
    Optional<Bundle> findByTeamAndTitle(Team team, String title);

    List<Bundle> findAllByTeam(Team team);

    Boolean existsByTitleAndTeam(String title, Team team);
    Boolean existsByPerformer(Member performer);

}
