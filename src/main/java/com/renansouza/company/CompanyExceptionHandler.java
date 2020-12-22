package com.renansouza.company;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.hateoas.Link;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;

@Singleton
@Produces(value = MediaType.APPLICATION_JSON)
@Requires(classes = {CompanyNotFoundException.class, CompanyException.class, ExceptionHandler.class})
public class CompanyExceptionHandler implements ExceptionHandler<RuntimeException, HttpResponse<Object>> {

    @Override
    public HttpResponse<Object> handle(HttpRequest request, RuntimeException exception) {
        MutableHttpResponse<Object> response = null;

        if (exception instanceof CompanyException) {
            response = HttpResponse.status(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        if (exception instanceof CompanyNotFoundException) {
            response = HttpResponse.status(HttpStatus.NOT_FOUND, exception.getMessage());
        }

        JsonError body = new JsonError("Exception: " + exception.getMessage());
        body.link(Link.SELF, Link.of(request.getUri()));
        response.body(body);

        return response;
    }
}