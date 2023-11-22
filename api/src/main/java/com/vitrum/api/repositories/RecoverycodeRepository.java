package com.vitrum.api.repositories;

import com.vitrum.api.data.models.User;
import com.vitrum.api.data.submodels.Recoverycode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RecoverycodeRepository extends MongoRepository<Recoverycode, String> {

    Optional<Recoverycode> findByUser(User user);
}
