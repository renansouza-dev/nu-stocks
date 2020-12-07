package com.renansouza.rf;

import com.renansouza.config.Constants;
import com.renansouza.config.DefaultController;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
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
import java.time.LocalDate;
import java.util.List;

@Validated
@Tag(name = "fixed income")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller(Constants.FIXED_INCOME_ROOT)
public class FixedIncomeController extends DefaultController {

    @Inject
    private FixedIncomeService service;

    @Operation(summary = "Add a new fixed Income", description = "A new fixed income is returned")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type="FixedIncome")))
    @ApiResponse(responseCode = "201", description = "Fixed income created.")
    @ApiResponse(responseCode = "404", description = "Invalid Fixed income supplied.")
    @Post(consumes = MediaType.APPLICATION_JSON)
    MutableHttpResponse<FixedIncome> saveFixedIncome(@Parameter(description="The fixed income data.") @Body @Valid FixedIncome fixedIncome) {
        final var savedFixedIncome = service.save(fixedIncome);

        return HttpResponse
                .created(savedFixedIncome)
                .header(HttpHeaders.LOCATION, location(Constants.FIXED_INCOME_ROOT, savedFixedIncome.getId()).getPath());
    }

//    @Operation(summary = "Get all fixed Income saved", description = "Get all fixed Income saved is returned")
//    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type="FixedIncome")))
//    @ApiResponse(responseCode = "201", description = "All fixed income is returned.")
//    @ApiResponse(responseCode = "404", description = "No fixed income was found.")
//    @Get(value = "/{date}", produces = MediaType.APPLICATION_JSON)
    @Get
    List<FixedIncome> getCompanies() {
        return service.calcIncome(null);
    }
}
