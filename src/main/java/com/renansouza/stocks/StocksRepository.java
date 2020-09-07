package com.renansouza.stocks;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
interface StocksRepository extends CrudRepository<Stocks, Long> {

    Stocks findByTicker(String ticker);

}