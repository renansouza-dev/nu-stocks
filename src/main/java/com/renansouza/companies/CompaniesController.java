package com.renansouza.companies;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Validated
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(CompaniesConstants.COMPANIES_ROOT)
public class CompaniesController {

    //TODO Add a service layer

    @Inject
    CompaniesRepository companiesRepository;

    //TODO Add pagination and sorting
    //TODO Add filtering to not shown deleted companies as default
    @Get
    List<Companies> getCompanies() {
        return StreamSupport
                .stream(companiesRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Get("/{id}")
    Companies getCompany(long id) {
        return companiesRepository.findById(id).orElse(null);
    }

    @Post(consumes = MediaType.APPLICATION_JSON)
    HttpResponse<?> saveCompany(@Body @Valid Companies company) {
        final var savedCompany = companiesRepository.save(company);

        return HttpResponse
                .created(savedCompany)
                .header(HttpHeaders.LOCATION, location(savedCompany.getId()).getPath());
    }

    @Put(consumes = MediaType.APPLICATION_JSON)
    HttpResponse<?> updateCompany(@Body @Valid Companies company) {
        final var updatedCompany = companiesRepository.update(company);

        return HttpResponse
                .noContent()
                .header(HttpHeaders.LOCATION, location(updatedCompany.getId()).getPath());
    }

    @Delete("/{id}")
    HttpResponse<?> deleteCompany(long id) {
        companiesRepository.findById(id).ifPresent(
                company -> {
                    company.setDeleted(true);
                    companiesRepository.update(company);
                }
        );

        return HttpResponse.noContent();
    }

    protected URI location(Long id) {
        return URI.create(CompaniesConstants.COMPANIES_ROOT + "/" + id);
    }

}