package com.renansouza.company;

import com.google.gson.Gson;
import com.renansouza.config.Constants;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.annotation.TransactionMode;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest(transactionMode = TransactionMode.SEPARATE_TRANSACTIONS)
class CompanyControllerTest {

    @Inject
    @Client(Constants.COMPANIES_ROOT)
    private HttpClient client;

    @Inject
    private CompanyRepository repository;

    @Value("${micronaut.user.name}")
    String username;

    @Value("${micronaut.user.password}")
    String password;

    @Test
    void getCompanies() {
        when(repository.findAll()).thenReturn(Collections.singletonList(getCompanySaved()));

        final var request = HttpRequest.GET("/").basicAuth(username, password);
        final var companies = client.toBlocking().exchange(request, Argument.of(List.class));

        assertNotNull(companies);
        assertNotNull(companies.getBody());
        assertEquals(1, companies.body().size());
        assertEquals(companies.getStatus().getCode(), HttpStatus.OK.getCode());
    }

    @Test
    void getCompanyNotFound() {
        final var exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final var request = HttpRequest.GET("/1").basicAuth(username, password);
            client.toBlocking().exchange(request);
        });

        assertEquals(exception.getStatus().getCode(), HttpStatus.NOT_FOUND.getCode());
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT).contains("company not found with id 1."));
    }

    private static Stream<Arguments> saveInvalidCompanyAndFail() {
        return Stream.of(
                Arguments.of("", "BANCO C6 S.A.", "Registration must be equal to 14 digits.", HttpStatus.BAD_REQUEST),
                Arguments.of(" ", "BANCO C6 S.A.", "Registration must be equal to 14 digits.", HttpStatus.BAD_REQUEST),
                Arguments.of("3187249500017", "BANCO C6 S.A.", "Registration must be equal to 14 digits.", HttpStatus.BAD_REQUEST),
                Arguments.of("318724950001722", "BANCO C6 S.A.", "Registration must be equal to 14 digits.", HttpStatus.BAD_REQUEST),
                Arguments.of("31872495000172", "", "Company name was not provided.", HttpStatus.BAD_REQUEST),
                Arguments.of("31872495000172", " ", "Company name was not provided.", HttpStatus.BAD_REQUEST)
        );
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("Test constrain exception while posting a company.")
    void saveInvalidCompanyAndFail(String registration, String name, String message, HttpStatus status) {
        final var exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final var company = new Company();
            company.setRegistration(registration);
            company.setName(name);
            company.setBank(true);

            when(repository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
            when(repository.findByRegistration(anyString())).thenReturn(Optional.empty());

            final var request = HttpRequest.POST("/", new Gson().toJson(company)).basicAuth(username, password);
            client.toBlocking().exchange(request);
        });

        assertEquals(exception.getStatus(), status);
        assertTrue(exception.getMessage().contains(message));
    }

    private static Stream<Arguments> saveDuplicateCompanyAndFail() {
        return Stream.of(
                Arguments.of(Optional.of(new Company()), Optional.empty(), true, "Company already saved"),
                Arguments.of(Optional.empty(), Optional.of(new Company()), true, "Company already saved"),
                Arguments.of(Optional.empty(), Optional.empty(), true, "All booleans fields can't have the same value"),
                Arguments.of(Optional.empty(), Optional.empty(), false, "All booleans fields can't have the same value")
        );
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("Test service exception while posting a company.")
    void saveDuplicateCompanyAndFail(Optional<Company> optional1, Optional<Company> optional2, boolean is, String message) {
        final var exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final var company = new Company();

            company.setRegistration("31872495000172");
            company.setName("BANCO C6 S.A.");
            company.setBank(is);
            company.setBroker(is);
            company.setManager(is);
            company.setAdministrator(is);
            company.setListedCompany(is);

            when(repository.findByNameIgnoreCase(anyString())).thenReturn(optional1);
            when(repository.findByRegistration(anyString())).thenReturn(optional2);

            HttpRequest<?> request = HttpRequest.POST("/", new Gson().toJson(company)).basicAuth(username, password);
            client.toBlocking().exchange(request);
        });

        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
        assertTrue(exception.getMessage().contains(message));
    }

    @Test
    @DisplayName("Test constrain exception while posting company.")
    void saveCompanyAndFail() {
        HttpClientResponseException exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final var company = new Company();

            company.setId(1L);
            company.setRegistration("31872495000172");
            company.setName("BANCO C6 S.A.");
            company.setBank(true);

            final var request = HttpRequest.POST("/", new Gson().toJson(company)).basicAuth(username, password);
            client.toBlocking().exchange(request, Argument.of(Company.class), Argument.of(CompanyException.class));
        });

        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
        assertTrue(exception.getMessage().contains("must be null"));
    }

    @Test
    void saveCompany() {
        when(repository.save(any(Company.class))).thenReturn(getCompanySaved());

        HttpRequest<?> request = HttpRequest.POST("/", new Gson().toJson(getCompanyTOSave())).basicAuth(username, password);
        final var response = client.toBlocking().exchange(request);

        assertNotNull(response);
        assertEquals(201, response.getStatus().getCode());
        assertEquals(Constants.COMPANIES_ROOT + "/1", response.getHeaders().get(HttpHeaders.LOCATION));
    }

    @Test
    void updateCompanyAndFailWithouId() {
        HttpClientResponseException exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final var company = getCompanyTOSave();
            company.setName("C6 BANK");

            final var request = HttpRequest.PUT("/", new Gson().toJson(company)).basicAuth(username, password);
            client.toBlocking().exchange(request);

        });

        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
        assertTrue(exception.getMessage().contains("Company id was not provided."));
    }

    @Test
    void updateCompanyAndFailWithoutName() {
        HttpClientResponseException exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final var company = getCompanySaved();
            company.setName(null);

            final var request = HttpRequest.PUT("/", new Gson().toJson(company)).basicAuth(username, password);
            client.toBlocking().exchange(request);

        });

        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
        assertTrue(exception.getMessage().contains("Company name was not provided."));
    }

    @Test
    void updateCompanyAndFailMissingCompany() {
        final var exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final var company = getCompanySaved();

            final var request = HttpRequest.PUT("/", new Gson().toJson(company)).basicAuth(username, password);
            client.toBlocking().exchange(request, Argument.of(Company.class), Argument.of(CompanyNotFoundException.class));
        });

        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT).contains("not found"));
    }

    @Test
    void updateCompany() {
        final var company = getCompanySaved();
        company.setName("C6 Bank");

        when(repository.findById(anyLong())).thenReturn(Optional.of(getCompanySaved()));

        final var request = HttpRequest.PUT("/", new Gson().toJson(company)).basicAuth(username, password);
        final var exchange = client.toBlocking().exchange(request);

        assertNotNull(exchange);
        assertEquals(exchange.getStatus(), HttpStatus.NO_CONTENT);
    }

    @Test
    void findCompanyAndFail() {
        HttpClientResponseException exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            final var company = getCompanySaved();
            company.setName("C6 BANK");

            final var request = HttpRequest.PUT("/", new Gson().toJson(company)).basicAuth(username, password);
            client.toBlocking().exchange(request, Argument.of(Company.class), Argument.of(CompanyNotFoundException.class));

        });

        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
        assertTrue(exception.getMessage().toLowerCase(Locale.ROOT).contains("not found"));
    }

    @Test
    void deleteCompany() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(getCompanySaved()));

        var request = HttpRequest.DELETE("/1").basicAuth(username, password);
        var response = client.toBlocking().exchange(request, Argument.of(Company.class));

        assertNotNull(response);
        assertEquals(204, response.getStatus().getCode());
        assertEquals(Constants.COMPANIES_ROOT + "/1", response.getHeaders().get(HttpHeaders.LOCATION));
    }

    private Company getCompanyTOSave() {
        final var company = new Company();
        company.setRegistration("31872495000172");
        company.setName("BANCO C6 S.A.");
        company.setBank(true);

        return company;
    }

    private Company getCompanySaved() {
        final var savedCompany = new Company();
        savedCompany.setId(1L);
        savedCompany.setRegistration("31872495000172");
        savedCompany.setName("BANCO C6 S.A.");
        savedCompany.setBank(true);

        return savedCompany;
    }

    @MockBean(CompanyRepository.class)
    CompanyRepository repository() {
        return mock(CompanyRepository.class);
    }

}