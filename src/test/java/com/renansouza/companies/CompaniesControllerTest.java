package com.renansouza.companies;

import com.google.gson.Gson;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.*;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompaniesControllerTest {

    @Inject
    @Client(CompaniesConstants.COMPANIES_ROOT)
    private HttpClient client;

    @Test
    @Order(1)
    void getCompaniesEmpty() {
        HttpRequest<?> request = HttpRequest.GET("/");
        final var companies = client.toBlocking().exchange(request);

        assertNotNull(companies);
        assertEquals(200, companies.getStatus().getCode());
    }

    @Test
    @Order(2)
    void getCompanyNotFound() {
        final var exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            HttpRequest<?> request = HttpRequest.GET("/1");
            final var company = client.toBlocking().exchange(request);
        });

        assertEquals(404, exception.getStatus().getCode());
        assertEquals("Page Not Found", exception.getMessage());
    }

    @Test
    @Order(3)
    void saveCompany() {
        final var company = new Companies();

        company.setRegistration("61532644000115");
        company.setName("ITAUSA INVESTIMENTOS ITAU S.A.");

        company.setListining(Listining.N2);
        company.setSector("Financeiro e Outros");
        company.setSubSector("Intermediarios Financeiros");
        company.setSegment("Bancos");

        HttpRequest<?> request = HttpRequest.POST("/", new Gson().toJson(company));
        final var response = client.toBlocking().exchange(request);

        assertNotNull(response);
        assertEquals(201, response.getStatus().getCode());
        assertEquals("/companies/1", response.getHeaders().get(HttpHeaders.LOCATION));
    }

    @Test
    @Order(4)
    void updateCompany() {
        var request = HttpRequest.GET("/1");
        var response = client.toBlocking().exchange(request, Argument.of(Companies.class));

        assertNotNull(response);
        assertEquals(200, response.getStatus().getCode());

        final var newName = "ITSA3";

        response.getBody().ifPresent(itsa -> itsa.setName(newName));
        request = HttpRequest.PUT("/", new Gson().toJson(response.getBody().get()));
        response = client.toBlocking().exchange(request);

        final var location = response.getHeaders().get("LOCATION");
        assertEquals("/companies/1", location);
        assertEquals(204, response.getStatus().getCode());
    }

    @Test
    @Order(5)
    void deleteCompany() {
        var request = HttpRequest.DELETE("/1");
        var companies = client.toBlocking().exchange(request, Argument.of(Companies.class));

        assertNotNull(companies);
        assertEquals(204, companies.getStatus().getCode());
    }

}