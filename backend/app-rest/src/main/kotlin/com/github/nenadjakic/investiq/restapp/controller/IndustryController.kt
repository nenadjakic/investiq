package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.common.dto.IndustryResponse
import com.github.nenadjakic.investiq.service.CountryService
import com.github.nenadjakic.investiq.service.IndustryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Industry Controller", description = "Endpoints for managing industries")
@RestController
@RequestMapping("/industry")
class IndustryController(
    private val industryService: IndustryService
) {

    @Operation(
        summary = "Find all industries",
        description = "Returns a list of industries",
        operationId = "findAllIndustries",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of industries"
            )
        ]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAll(): ResponseEntity<List<IndustryResponse>> =
        ResponseEntity.ok(industryService.findAll())
}