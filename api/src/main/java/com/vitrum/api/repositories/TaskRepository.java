package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByTitleAndBundle(String title, Bundle bundle);

    Boolean existsByTitleAndBundle(String title, Bundle bundle);
}
