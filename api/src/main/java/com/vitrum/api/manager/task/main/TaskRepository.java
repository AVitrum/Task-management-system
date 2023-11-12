package com.vitrum.api.manager.task.main;

import com.vitrum.api.manager.bundle.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByTitleAndBundle(String title, Bundle bundle);

    Boolean existsByTitleAndBundle(String title, Bundle bundle);
}
