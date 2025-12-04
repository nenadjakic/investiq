package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.CurrencyResponse
import com.github.nenadjakic.investiq.service.CurrencyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Currency Controller", description = "Endpoints for managing currencies")
@RestController
@RequestMapping("/currency")
class CurrencyController(
    private val currencyService: CurrencyService
) {

    @Operation(
        summary = "Find all currencies",
        description = "Returns a list of currencies",
        operationId = "findAllCurrencies",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of currencies"
            )
        ]
    )
    @GetMapping
    fun getAll(): ResponseEntity<List<CurrencyResponse>> =
        ResponseEntity.ok(currencyService.findAll())
}