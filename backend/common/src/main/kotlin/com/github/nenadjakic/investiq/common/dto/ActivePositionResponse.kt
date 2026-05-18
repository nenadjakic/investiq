package com.github.nenadjakic.investiq.common.dto

import com.github.nenadjakic.investiq.data.enum.AssetType
import com.github.nenadjakic.investiq.data.enum.Platform
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "Active portfolio position with invested/market values and P/L")
data class ActivePositionResponse(

    @param:Schema(description = "Trading platform", example = "IBKR", allowableValues = ["TRADING212", "IBKR", "REVOLUT", "ETORO"])
    val platform: Platform?,

    @param:Schema(description = "Asset type", example = "STOCK", allowableValues = ["STOCK", "ETF"])
    val type: AssetType,

    @param:Schema(description = "Stock ticker symbol", example = "AAPL")
    val ticker: String,

    @param:Schema(description = "Company name", example = "Apple Inc.")
    val name: String,

    @param:Schema(description = "Number of shares", example = "50")
    val shares: BigDecimal,

    @param:Schema(description = "Average purchase price (EUR)", example = "150.00")
    val avgPriceEur: BigDecimal,

    @param:Schema(description = "Total invested amount (EUR)", example = "7500.00")
    val investedEur: BigDecimal,

    @param:Schema(description = "Invested percentage of total invested (%%)", example = "12.50")
    val investedPercentage: BigDecimal,

    @param:Schema(description = "Unrealized profit/loss (EUR)", example = "1250.00")
    val profitLossEur: BigDecimal,

    @param:Schema(description = "Unrealized profit/loss percentage (%%)", example = "16.67")
    val profitLossPercentage: BigDecimal,

    @param:Schema(description = "Realized profit/loss (EUR)", example = "300.00")
    val realizedProfitLossEur: BigDecimal,

    @param:Schema(description = "Total dividends (EUR)", example = "120.00")
    val dividendsEur: BigDecimal,

    @param:Schema(description = "Total fees (EUR)", example = "45.00")
    val feesEur: BigDecimal,

    @param:Schema(description = "Total profit/loss including unrealized + realized + dividends - fees (EUR)", example = "1625.00")
    val totalProfitLossEur: BigDecimal,

    @param:Schema(description = "Total profit/loss percentage (%%)", example = "21.67")
    val totalProfitLossPercentage: BigDecimal,

    @param:Schema(description = "Market value (EUR)", example = "8750.00")
    val marketValueEur: BigDecimal,

    @param:Schema(description = "Market value percentage of total portfolio (%%)", example = "7.50")
    val marketValuePercentage: BigDecimal
)
