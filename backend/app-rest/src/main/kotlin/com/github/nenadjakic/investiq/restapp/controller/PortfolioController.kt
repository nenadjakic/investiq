package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.AssetHoldingResponse
import com.github.nenadjakic.investiq.common.dto.AssetTypeValueResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioChartResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioSummaryResponse
import com.github.nenadjakic.investiq.common.dto.IndustrySectorValueResponse
import com.github.nenadjakic.investiq.common.dto.MonthlyInvestedResponse
import com.github.nenadjakic.investiq.common.dto.CountryValueResponse
import com.github.nenadjakic.investiq.common.dto.CurrencyValueResponse
import com.github.nenadjakic.investiq.common.dto.MonthlyDividendResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioPerformanceResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioAllocationResponse
import com.github.nenadjakic.investiq.common.dto.DividendCostYieldResponse
import com.github.nenadjakic.investiq.service.PortfolioService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Portfolio Controller", description = "Portfolio overview, holdings and performance analytics")
@Validated
@RestController
@RequestMapping("/portfolio")
class PortfolioController(
    private val portfolioService: PortfolioService
) {

    @Operation(
        operationId = "getPortfolioSummary",
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
        description = "Returns daily market value and invested amount for the specified time period, optionally including benchmark indices"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved value series"),
            ApiResponse(responseCode = "400", description = "Invalid days parameter")
        ]
    )
    @GetMapping("/chart/performance", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPortfolioPerformanceChart(
        @RequestParam(required = false) days: Int?
    ): ResponseEntity<PortfolioChartResponse> =
        ResponseEntity.ok(portfolioService.getPortfolioValueSeries(days))

    @Operation(
        operationId = "getIndustrySectorAllocation",
        summary = "Get portfolio allocation by industry and sector",
        description = "Returns aggregated market value grouped by industry and sector for the latest snapshot"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved allocation",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = IndustrySectorValueResponse::class)
                )]
            )
        ]
    )
    @GetMapping("/allocation/industry-sector", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getIndustrySectorAllocation(): ResponseEntity<List<IndustrySectorValueResponse>> =
        ResponseEntity.ok(portfolioService.getIndustrySectorAllocation())

    @Operation(
        operationId = "getMonthlyInvested",
        summary = "Get monthly invested amounts",
        description = "Returns invested euros per month for the last N months"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved monthly invested amounts",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = MonthlyInvestedResponse::class)
                )]
            )
        ]
    )
    @GetMapping("/monthly-invested", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMonthlyInvested(
        @RequestParam(required = false) months: Int?): ResponseEntity<MonthlyInvestedResponse> =
        ResponseEntity.ok(portfolioService.getMonthlyInvested(months))

    @Operation(
        operationId = "getMonthlyDividends",
        summary = "Get monthly dividends",
        description = "Returns dividends per month for the last N months (null = entire history)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved monthly dividends",
                content = [Content()]
            )
        ]
    )
    @GetMapping("/monthly-dividends", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMonthlyDividends(
        @RequestParam(required = false) months: Int?): ResponseEntity<MonthlyDividendResponse> =
        ResponseEntity.ok(portfolioService.getMonthlyDividends(months))

    @Operation(
        operationId = "getPortfolioPerformanceData",
        summary = "Get combined performance data",
        description = "Returns the portfolio chart series and monthly invested amounts together"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved performance data"),
            ApiResponse(responseCode = "400", description = "Invalid parameters")
        ]
    )
    @GetMapping("/performance-data", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPortfolioPerformanceData(
        @RequestParam(required = false) days: Int?,
        @RequestParam(required = false) months: Int?
    ): ResponseEntity<PortfolioPerformanceResponse> {
        val chart = portfolioService.getPortfolioValueSeries(days)
        val monthly = portfolioService.getMonthlyInvested(months)
        return ResponseEntity.ok(PortfolioPerformanceResponse(chart = chart, monthlyInvested = monthly))
    }

    @Operation(
        operationId = "getCombinedAllocation",
        summary = "Get combined allocation",
        description = "Returns allocations by currency, industry/sector and country together"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved allocation")
        ]
    )
    @GetMapping("/allocation", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllocation(): ResponseEntity<PortfolioAllocationResponse> {
        val currency = portfolioService.getCurrencyExposure()
        val industry = portfolioService.getIndustrySectorAllocation()
        val country = portfolioService.getCountryAllocation()
        val assetType = portfolioService.getAssetTypeAllocation()
        return ResponseEntity.ok(
            PortfolioAllocationResponse(
                byCurrency = currency,
                byIndustrySector = industry,
                byCountry = country,
                byAssetType = assetType
            )
        )
    }

    @Operation(
        operationId = "getCountryAllocation",
        summary = "Get portfolio allocation by country",
        description = "Returns aggregated market value grouped by company country for the latest snapshot"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved allocation",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CountryValueResponse::class)
                )]
            )
        ]
    )
    @GetMapping("/allocation/country", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getCountryAllocation(): ResponseEntity<List<CountryValueResponse>> =
        ResponseEntity.ok(portfolioService.getCountryAllocation())

    @Operation(
        operationId = "getCurrencyExposure",
        summary = "Get portfolio exposure by currency",
        description = "Returns aggregated market value grouped by asset currency for the latest snapshot"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved exposure",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CurrencyValueResponse::class)
                )]
            )
        ]
    )
    @GetMapping("/allocation/currency", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getCurrencyExposure(): ResponseEntity<List<CurrencyValueResponse>> =
        ResponseEntity.ok(portfolioService.getCurrencyExposure())

    @Operation(
        operationId = "getAssetTypeAllocation",
        summary = "Get portfolio allocation by asset type",
        description = "Returns aggregated market value grouped by asset type for the latest snapshot"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved allocation", content = [Content(mediaType = "application/json", schema = Schema(implementation = AssetTypeValueResponse::class))])
        ]
    )
    @GetMapping("/allocation/asset-type", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAssetTypeAllocation(): ResponseEntity<List<AssetTypeValueResponse>> =
        ResponseEntity.ok(portfolioService.getAssetTypeAllocation())

    @Operation(
    summary = "Get current portfolio holdings",
    description = "Returns a list of all current portfolio positions with detailed information."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved holdings list",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = AssetHoldingResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    @GetMapping("/holdings", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getHoldings(): ResponseEntity<List<AssetHoldingResponse>> =
         ResponseEntity.ok(portfolioService.getPortfolioHoldings())

    @Operation(
        operationId = "getTopBottomPerformers",
        summary = "Get top and bottom performers",
        description = "Returns top and bottom performing assets with their percentage change"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved performers"),
            ApiResponse(responseCode = "400", description = "Invalid limit parameter")
        ]
    )
    @GetMapping("/performers", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTopBottomPerformers(
         @Min(1) @RequestParam(required = false, defaultValue = "5") limit: Int
    ): ResponseEntity<com.github.nenadjakic.investiq.common.dto.TopBottomPerformersResponse> =
        ResponseEntity.ok(portfolioService.getTopBottomPerformers(limit))

    @Operation(
        operationId = "getActivePositions",
        summary = "Get active positions summary",
        description = "Returns active positions with invested amount, invested %, P/L (EUR and %), market value and market value %"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved positions", content = [Content(mediaType = "application/json", schema = Schema(implementation = com.github.nenadjakic.investiq.common.dto.ActivePositionResponse::class))]),
            ApiResponse(responseCode = "500", description = "Internal server error", content = [Content()])
        ]
    )
    @GetMapping("/holdings/summary", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getActivePositions(): ResponseEntity<List<com.github.nenadjakic.investiq.common.dto.ActivePositionResponse>> =
        ResponseEntity.ok(portfolioService.getActivePositions())

    @Operation(
        operationId = "getDividendCostYield",
        summary = "Get dividend cost yield",
        description = "Returns dividend cost yield for each asset and total portfolio. Yield is calculated as (Annualized Dividend / Cost Basis) * 100, annualized from first purchase date."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved dividend cost yield",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = DividendCostYieldResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    @GetMapping("/dividend-cost-yield", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDividendCostYield(): ResponseEntity<DividendCostYieldResponse> =
        ResponseEntity.ok(portfolioService.getDividendCostYield())

 }
