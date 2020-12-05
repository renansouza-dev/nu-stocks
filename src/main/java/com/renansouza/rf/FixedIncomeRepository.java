package com.renansouza.rf;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface FixedIncomeRepository extends CrudRepository<FixedIncome, Long> {

}