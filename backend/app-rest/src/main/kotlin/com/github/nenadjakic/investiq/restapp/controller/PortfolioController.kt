package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.PortfolioChartResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioSummaryResponse
import com.github.nenadjakic.investiq.service.PortfolioService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate

@Tag(name = "Portfolio Controller", description = "Portfolio overview, holdings and performance analytics")
@RestController
@RequestMapping("/portfolio")
class PortfolioController(
    private val portfolioService: PortfolioService
) {

    @Operation(
        summary = "Get portfolio summary",
        description = "Returns aggregated portfolio statistics including total value, returns, and changes"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved portfolio summary",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = PortfolioSummaryResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    @GetMapping("/summary", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPortfolioSummary(): ResponseEntity<PortfolioSummaryResponse> =
        ResponseEntity.ok(portfolioService.getPortfolioSummary())

    @Operation(
        operationId = "getPortfolioPerformanceChart",
        summary = "Get portfolio value time series",
        description = "Returns daily market value and invested amount for the specified time period"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved value series"),
            ApiResponse(responseCode = "400", description = "Invalid days parameter")
        ]
    )
    @GetMapping("/chart/performance", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPortfolioPerformanceChart(
        @RequestParam(required = false) days: Int = 365
    ): ResponseEntity<PortfolioChartResponse> =
        ResponseEntity.ok(portfolioService.getPortfolioValueSeries(days))
}