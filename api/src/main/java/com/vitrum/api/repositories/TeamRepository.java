package com.vitrum.api.repositories;

import com.vitrum.api.models.Team;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TeamRepository extends MongoRepository<Team, String> {

    Optional<Team> findByName(String name);

    Boolean existsByName(String name);
}
