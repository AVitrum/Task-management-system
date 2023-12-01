package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.submodels.OldTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OldTaskRepository extends JpaRepository<OldTask, Long> {

    Optional<OldTask> findByTask(Task task);

    Optional<OldTask> findByTaskAndVersion(Task task, Long version);

    Optional<List<OldTask>> findAllByTask(Task task);

//    Optional<OldTask> findByTitleAndCreatorAndVersion(String title, Member creator, Long version);
}
