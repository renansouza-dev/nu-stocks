package com.renansouza.stocks;

import com.google.gson.Gson;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
class StocksControllerTest {

    @Inject
    @Client("/stocks")
    private HttpClient client;

    @Test
    void getStocksEmpty() {
        HttpRequest<?> request = HttpRequest.GET("/");
        final var stocks = client.toBlocking().exchange(request);

        assertNotNull(stocks);
        assertEquals(204, stocks.getStatus().getCode());
    }

    @Test
    void getStockNotFound() {
        HttpRequest<?> request = HttpRequest.GET("/ITSA4");
        final var stock = client.toBlocking().exchange(request);

        assertNotNull(stock);
        assertEquals(404, stock.getStatus().getCode());
    }

    @Test
    void saveStock() {
        final var stock = new Stocks();

        stock.setTicker("ITSA4");
        stock.setName("ITAUSA INVESTIMENTOS ITAU S.A.");

        stock.setListining(Listining.N2);
        stock.setSector("Financeiro e Outros");
        stock.setSubSector("Intermediarios Financeiros");
        stock.setSegment("Bancos");

        stock.setFreeFloat(100.00);

        HttpRequest<?> request = HttpRequest.POST("/", new Gson().toJson(stock));
        final var stocks = client.toBlocking().exchange(request);

        assertNotNull(stocks);
        assertEquals(201, stocks.getStatus().getCode());
    }

}