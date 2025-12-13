package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.common.dto.ExchangeResponse
import com.github.nenadjakic.investiq.service.ExchangeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Exchange Controller", description = "Endpoints for managing exchanges")
@RestController
@RequestMapping("/exchange")
class ExchangeController(
    private val exchangeService: ExchangeService
) {

    @Operation(
        summary = "Find all exchanges",
        description = "Returns a list of exchanges",
        operationId = "findAllExchanges",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of exchanges"
            )
        ]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAll(): ResponseEntity<List<ExchangeResponse>> =
        ResponseEntity.ok(exchangeService.findAll())
}