package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.service.CountryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Country Controller", description = "Endpoints for managing countries")
@RestController
@RequestMapping("/country")
class CountryController(
    private val countryService: CountryService
) {

    @Operation(
        summary = "Find all countries",
        description = "Returns a list of countries",
        operationId = "findAllCountries",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of countries"
            )
        ]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAll(): ResponseEntity<List<CountryResponse>> =
        ResponseEntity.ok(countryService.findAll())
}