package com.renansouza.stocks;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Validated
@Controller("/stocks")
public class StocksController {

    @Inject
    StocksRepository stocksRepository;

    @Get
    HttpResponse<?> getStocks() {
        final var stocks = StreamSupport
                .stream(stocksRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        if (stocks.isEmpty()) {
            return HttpResponse.noContent();
        }

        return HttpResponse.ok(stocks);
    }

    @Get("/{ticker}")
    HttpResponse<?> getStock(String ticker) {
        final var stock = stocksRepository.findByTicker(ticker.toUpperCase());

        if (stock.getId() == null) {
            return HttpResponse.notFound();
        }

        return HttpResponse.ok(stock);
    }

    @Post(consumes = MediaType.APPLICATION_JSON)
    HttpResponse<?> saveStock(@Body @Valid Stocks stock) {
        final var savedStock = stocksRepository.save(stock);
        if (savedStock.getId() == null) {
            return HttpResponse.badRequest("Couldn't create a new stock with the provided information.");
        }

        return HttpResponse.created(String.format("Stock created successfully with id %d.", savedStock.getId()));
    }

}