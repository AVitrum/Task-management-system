package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Team;
import com.vitrum.api.data.submodels.TeamStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamStageRepository extends JpaRepository<TeamStage, Long> {

    Optional<TeamStage> findByTeamAndIsCurrent(Team team, Boolean isCurrent);
    Optional<TeamStage> findByTeamAndNumber(Team team, Long number);

    Boolean existsByTeamAndIsCurrent(Team team, Boolean isCurrent);
}
