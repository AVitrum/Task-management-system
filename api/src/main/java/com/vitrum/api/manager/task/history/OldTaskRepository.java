package com.vitrum.api.manager.task.history;

import com.vitrum.api.manager.task.main.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OldTaskRepository extends JpaRepository<OldTask, Long> {

    Optional<OldTask> findByTask(Task task);

    Optional<OldTask> findByTaskAndVersion(Task task, Long version);

    Optional<List<OldTask>> findAllByTask(Task task);

//    Optional<OldTask> findByTitleAndCreatorAndVersion(String title, Member creator, Long version);
}
