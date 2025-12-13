package com.github.nenadjakic.investiq.common.dto

data class PortfolioPerformanceResponse(
    val chart: PortfolioChartResponse,
    val monthlyInvested: MonthlyInvestedResponse
)

