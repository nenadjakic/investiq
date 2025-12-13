package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.SectorResponse
import com.github.nenadjakic.investiq.service.SectorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Sector Controller", description = "Endpoints for managing sectors")
@RestController
@RequestMapping("/sector")
class SectorController(
    private val sectorService: SectorService
) {

    @Operation(
        summary = "Find all sectors",
        description = "Returns a list of sectors",
        operationId = "findAllSectors",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of sectors"
            )
        ]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAll(): ResponseEntity<List<SectorResponse>> =
        ResponseEntity.ok(sectorService.findAll())
}