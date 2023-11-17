package com.vitrum.api.repositories;

import com.vitrum.api.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    Boolean existsByEmailOrUsername(String email, String username);
}
