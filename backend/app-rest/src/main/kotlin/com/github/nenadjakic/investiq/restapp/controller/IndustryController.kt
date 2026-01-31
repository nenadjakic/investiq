package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.industry.IndustryAddRequest
import com.github.nenadjakic.investiq.common.dto.industry.IndustryResponse
import com.github.nenadjakic.investiq.service.IndustryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

@Tag(name = "Industry Controller", description = "Endpoints for managing industries")
@RestController
@RequestMapping("/industry")
@Validated
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

    @Operation(
        summary = "Find industry by id",
        description = "Returns an industry by its ID",
        operationId = "findIndustryById",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Industry found"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Industry not found"
            )
        ]
    )
    @GetMapping(value = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable id: UUID): ResponseEntity<IndustryResponse> {
        val industry = industryService.findById(id)
        return ResponseEntity.of(industry)
    }

    @Operation(
        summary = "Create a new industry",
        description = "Creates a new industry and returns 201 Created with the location of the new resource",
        operationId = "createIndustry",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Industry created successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data"
            )
        ]
    )
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(
        @Valid @RequestBody request: IndustryAddRequest
    ): ResponseEntity<Void> {
        val createdIndustry = industryService.create(request)
        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdIndustry.id)
            .toUri()
        return ResponseEntity.created(location).build()
    }
}