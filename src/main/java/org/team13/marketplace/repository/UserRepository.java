package org.team13.marketplace.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.team13.marketplace.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findBySessionToken(String sessionToken);
    boolean existsBySessionToken(String sessionToken);
}
