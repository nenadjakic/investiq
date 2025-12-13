package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.CompanyResponse
import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.service.CompanyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Company Controller", description = "Endpoints for managing companies")
@RestController
@RequestMapping("/company")
class CompanyController(
    private val companyService: CompanyService
) {

    @Operation(
        summary = "Find all companies",
        description = "Returns a list of companies",
        operationId = "findAllCompanies",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of companies"
            )
        ]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAll(): ResponseEntity<List<CompanyResponse>> =
        ResponseEntity.ok(companyService.findAll())
}