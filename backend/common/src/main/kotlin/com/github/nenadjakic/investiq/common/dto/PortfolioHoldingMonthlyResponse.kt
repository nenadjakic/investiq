package com.github.nenadjakic.investiq.common.dto

import java.math.BigDecimal

data class PortfolioHoldingMonthlyResponse(
    val yearMonth: String,
    val holdings: List<MonthlyHoldingEntry>
)

data class MonthlyHoldingEntry(
    val name: String,
    val tickers: List<String>,
    val marketValueEur: BigDecimal,
    val costBasisEur: BigDecimal,
    val unrealizedPlEur: BigDecimal,
    val unrealizedPlPercentage: BigDecimal,
    val portfolioPercentage: BigDecimal
)