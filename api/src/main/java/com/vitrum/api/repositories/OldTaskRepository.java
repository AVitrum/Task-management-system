package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Task;
import com.vitrum.api.data.submodels.OldTask;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OldTaskRepository extends MongoRepository<OldTask, String> {

    Optional<OldTask> findByTaskAndVersion(Task task, Long version);

    List<OldTask> findAllByTask(Task task);
}
