package com.renansouza.companies;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
interface CompaniesRepository extends CrudRepository<Companies, Long> {

}