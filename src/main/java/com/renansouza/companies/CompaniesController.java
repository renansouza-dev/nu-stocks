package com.renansouza.companies;

import com.renansouza.config.Constants;
import com.renansouza.config.DefaultController;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Validated
@Tag(name = "company")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(Constants.COMPANIES_ROOT)
public class CompaniesController extends DefaultController {

    //TODO Add a service layer

    @Inject
    CompaniesRepository companiesRepository;

    //TODO Add pagination and sorting
    //TODO Add filtering to not shown deleted companies as default
    //TODO Add swagger https://piotrminkowski.com/tag/micronaut/
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

    @Operation(summary = "Add a new company to the listing companies", description = "A new company is returned")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type="Companies")))
    @ApiResponse(responseCode = "201", description = "Company created.")
    @ApiResponse(responseCode = "404", description = "Invalid company supplied.")
    @Post(consumes = MediaType.APPLICATION_JSON)
    MutableHttpResponse<Companies> saveCompany(@Parameter(description="The company data.") @Body @Valid Companies company) {
        final var savedCompany = companiesRepository.save(company);

        return HttpResponse
                .created(savedCompany)
                .header(HttpHeaders.LOCATION, location(Constants.COMPANIES_ROOT, savedCompany.getId()).getPath());
    }

    @Put(consumes = MediaType.APPLICATION_JSON)
    MutableHttpResponse<Object> updateCompany(@Body @Valid Companies company) {
        final var updatedCompany = companiesRepository.update(company);

        return HttpResponse
                .noContent()
                .header(HttpHeaders.LOCATION, location(Constants.COMPANIES_ROOT, updatedCompany.getId()).getPath());
    }

    @Delete("/{id}")
    MutableHttpResponse<Object> deleteCompany(long id) {
        companiesRepository.findById(id).ifPresent(
                company -> {
                    company.setDeleted(true);
                    companiesRepository.update(company);
                }
        );

        return HttpResponse.noContent();
    }

}