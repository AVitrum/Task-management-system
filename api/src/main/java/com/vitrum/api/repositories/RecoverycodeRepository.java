package com.vitrum.api.repositories;

import com.vitrum.api.models.User;
import com.vitrum.api.models.submodels.Recoverycode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RecoverycodeRepository extends MongoRepository<Recoverycode, String> {
    Optional<Recoverycode> findByUser(User user);
}
