package com.vitrum.api.repositories;

import com.vitrum.api.data.models.Bundle;
import com.vitrum.api.data.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {

    Optional<Task> findByTitleAndBundle(String title, Bundle bundle);

    Boolean existsByTitleAndBundle(String title, Bundle bundle);

    List<Task> findAllByBundle(Bundle bundle);
}
