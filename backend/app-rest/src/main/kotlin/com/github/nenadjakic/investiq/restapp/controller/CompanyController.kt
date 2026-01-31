package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.company.CompanyAddRequest
import com.github.nenadjakic.investiq.common.dto.company.CompanyResponse
import com.github.nenadjakic.investiq.service.CompanyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Tag(name = "Company Controller", description = "Endpoints for managing companies")
@RestController
@RequestMapping("/company")
@Validated
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

    @Operation(
        summary = "Create a new company",
        description = "Creates a new company and returns 201 Created with the location of the new resource",
        operationId = "createCompany",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Company created successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data"
            )
        ]
    )
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@Valid @RequestBody request: CompanyAddRequest): ResponseEntity<Void> {
        companyService.create(request)
            .let {
                val location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(it.id)
                .toUri()
                return ResponseEntity.created(location).build()
            }
    }
}