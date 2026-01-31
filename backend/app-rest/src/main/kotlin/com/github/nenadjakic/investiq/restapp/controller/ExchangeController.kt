package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.exchange.ExchangeAddRequest
import com.github.nenadjakic.investiq.common.dto.exchange.ExchangeResponse
import com.github.nenadjakic.investiq.service.ExchangeService
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

@Tag(name = "Exchange Controller", description = "Endpoints for managing exchanges")
@RestController
@RequestMapping("/exchange")
@Validated
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

    @Operation(
        summary = "Create exchange",
        description = "Creates a new exchange",
        operationId = "createExchange",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Exchange created successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data"
            )
        ]
    )
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createExchange(@Valid @RequestBody request: ExchangeAddRequest): ResponseEntity<Void> {
        exchangeService.create(request)
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