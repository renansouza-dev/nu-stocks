package com.renansouza.config;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class AuthenticationProviderUserPasswordTest {

    @Inject
    @Client("/")
    private HttpClient client;

    @Value("${micronaut.user.name}")
    String username;

    @Value("${micronaut.user.password}")
    String password;

    @Test
    void testAuthenticationFailure() {
        //when: 'Accessing a secured URL without authenticating'
        Executable e = () -> client.toBlocking().exchange(HttpRequest.GET("/"));

        // then: 'returns unauthorized'
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, e);
        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());
        assertEquals("Unauthorized", thrown.getMessage());
    }

    @Test
    void testAuthenticationSuccess() {
        // 'Login endpoint is called with valid credentials'
        HttpRequest request = HttpRequest.POST("/login", new UsernamePasswordCredentials(username, password));
        HttpResponse<BearerAccessRefreshToken> rsp = client.toBlocking().exchange(request, BearerAccessRefreshToken.class);

        assertEquals(rsp.getStatus(), HttpStatus.OK);


        final Optional<BearerAccessRefreshToken> bearerAccessRefreshToken = rsp.getBody();
        assertNotNull(bearerAccessRefreshToken);
        assertTrue(bearerAccessRefreshToken.isPresent());
        assertEquals(username, bearerAccessRefreshToken.get().getUsername());

        final HttpResponse<String> healthCheck = client
                .toBlocking().exchange(HttpRequest.GET("/health")
                .accept(MediaType.APPLICATION_JSON).bearerAuth(bearerAccessRefreshToken.get().getAccessToken()), String.class);

        assertNotNull(healthCheck);
        assertEquals(healthCheck.getStatus(), HttpStatus.OK);
    }
}