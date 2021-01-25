package com.renansouza.user;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.transaction.annotation.ReadOnly;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @ReadOnly
    Optional<User> findByUsername(String username);

    @ReadOnly
    boolean existsByUsername(String username);

}