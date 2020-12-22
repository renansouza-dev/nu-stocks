package com.renansouza.company;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.transaction.annotation.ReadOnly;

import java.util.Optional;

@Repository
interface CompanyRepository extends CrudRepository<Company, Long> {

    @ReadOnly
    Optional<Company> findByNameIgnoreCase(String name);
    @ReadOnly
    Optional<Company> findByRegistration(String registration);

}