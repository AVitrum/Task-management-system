package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndTeam(Long id, Team team);

    List<Task> findAllByTeam(Team team);
}
