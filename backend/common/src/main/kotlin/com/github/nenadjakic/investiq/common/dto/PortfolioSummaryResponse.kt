package com.github.nenadjakic.investiq.common.dto

import com.github.nenadjakic.investiq.data.enum.AssetType
import com.github.nenadjakic.investiq.data.enum.Platform
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

    @param:Schema(description = "Total unrealized profit/loss", example = "12500.00")
    val totalUnrealizedPL: BigDecimal,

    @param:Schema(description = "Total realized profit/loss", example = "2930.00")
    val totalRealizedPL: BigDecimal,

    @param:Schema(description = "Total number of holdings/positions", example = "15")
    val totalHoldings: Int,

    @param:Schema(description = "Total dividends", example = "15")
    val totalDividends: BigDecimal,

    @param:Schema(description = "Annualized dividend cost yield percentage", example = "3.25")
    val dividendCostYield: BigDecimal,

    @param:Schema(description = "Period-based change information")
    val periodChange: PeriodChangeResponse
)

@Schema(description = "Change information for a specific period")
data class PeriodChangeResponse(
    @param:Schema(description = "Start date of the period", example = "2024-12-04")
    val startDate: LocalDate,

    @param:Schema(description = "End date of the period (snapshot date)", example = "2024-12-09")
    val endDate: LocalDate,

    @param:Schema(description = "Number of days in the period", example = "5")
    val periodDays: Int,

    @param:Schema(description = "Change amount in the period", example = "1234.56")
    val changeAmount: BigDecimal,

    @param:Schema(description = "Change percentage in the period", example = "2.45")
    val changePercentage: BigDecimal,

    @param:Schema(description = "Period label", example = "last 5 days", allowableValues = ["today", "last 5 days", "last week", "last month"])
    val periodLabel: String
)


@Schema(description = "Individual stock/etf holding details")
data class AssetHoldingResponse(

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

    @param:Schema(description = "Average purchase price", example = "150.00")
    val avgPrice: BigDecimal,

    @param:Schema(description = "Current market price", example = "175.50")
    val currentPrice: BigDecimal,

    @param:Schema(description = "Profit/Loss amount", example = "1275.00")
    val profitLoss: BigDecimal,

    @param:Schema(description = "Profit/Loss percentage", example = "17.0")
    val profitLossPercentage: BigDecimal,

    @param:Schema(description = "Percentage of total portfolio", example = "35.0")
    val portfolioPercentage: BigDecimal,

    @param:Schema(description = "Annualized dividend cost yield percentage", example = "2.50")
    val dividendCostYield: BigDecimal
)

@Schema(description = "Individual company/etf holding details")
data class CompanyAssetHoldingResponse(

    @param:Schema(description = "Trading platform", example = "IBKR", allowableValues = ["TRADING212", "IBKR", "REVOLUT", "ETORO"])
    val platform: Platform?,

    @param:Schema(description = "Stock ticker symbols", example = "MSFT, MSF")
    val tickers: List<String>,

    @param:Schema(description = "Company name or ETF name", example = "Apple Inc.")
    val name: String,

    @param:Schema(description = "Profit/Loss amount", example = "1275.00")
    val profitLoss: BigDecimal,

    @param:Schema(description = "Profit/Loss percentage", example = "17.0")
    val profitLossPercentage: BigDecimal,

    @param:Schema(description = "Percentage of total portfolio", example = "35.0")
    val portfolioPercentage: BigDecimal,

    @param:Schema(description = "Annualized dividend cost yield percentage", example = "2.50")
    val dividendCostYield: BigDecimal
)