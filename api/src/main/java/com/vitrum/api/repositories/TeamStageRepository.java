package com.vitrum.api.repositories;

import com.vitrum.api.data.submodels.TeamStage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamStageRepository extends JpaRepository<TeamStage, Long> {
}
