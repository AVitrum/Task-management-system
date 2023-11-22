package com.vitrum.api.repositories;

import com.vitrum.api.data.submodels.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {

    @Query("{$and: [{'user._id': ?0}, {$or: [{'expired': false}, {'revoked': false}]}]}")
    List<Token> findAllValidTokenByUser(String userId);

    @Query("{$and: [{'user._id': ?0}, {$or: [{'expired': true}, {'revoked': true}]}]}")
    List<Token> findAllExpiredTokenByUser(String userId);

    Optional<Token> findByToken(String token);
}
