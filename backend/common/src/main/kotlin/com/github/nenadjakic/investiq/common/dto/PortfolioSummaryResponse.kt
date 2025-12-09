package com.github.nenadjakic.investiq.common.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "Portfolio summary with total value and returns")
data class PortfolioSummaryResponse(
    @param:Schema(description = "Snapshot date", example = "2024-12-09")
    val snapshotDate: LocalDate,

    @param:Schema(description = "Total current portfolio value", example = "125430.00")
    val totalValue: BigDecimal,

    @param:Schema(description = "Total amount invested", example = "110000.00")
    val totalInvested: BigDecimal,

    @param:Schema(description = "Total return amount", example = "15430.00")
    val totalReturn: BigDecimal,

    @param:Schema(description = "Total return percentage", example = "14.05")
    val totalReturnPercentage: BigDecimal,

    @param:Schema(description = "Today's change amount", example = "1234.56")
    val todayChange: BigDecimal,

    @param:Schema(description = "Today's change percentage", example = "2.45")
    val todayChangePercentage: BigDecimal
)

@Schema(description = "Individual stock holding details")
data class HoldingDTO(
    @param:Schema(description = "Stock ticker symbol", example = "AAPL")
    val ticker: String,

    @param:Schema(description = "Company name", example = "Apple Inc.")
    val name: String,

    @param:Schema(description = "Number of shares", example = "50")
    val shares: Int,

    @param:Schema(description = "Average purchase price", example = "150.00")
    val avgPrice: BigDecimal,

    @param:Schema(description = "Current market price", example = "175.50")
    val currentPrice: BigDecimal,

    @param:Schema(description = "Profit/Loss amount", example = "1275.00")
    val profitLoss: BigDecimal,

    @param:Schema(description = "Profit/Loss percentage", example = "17.0")
    val profitLossPercentage: BigDecimal,

    @param:Schema(description = "Percentage of total portfolio", example = "35.0")
    val portfolioPercentage: BigDecimal
)