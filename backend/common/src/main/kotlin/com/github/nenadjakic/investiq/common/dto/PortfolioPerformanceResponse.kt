package com.github.nenadjakic.investiq.common.dto

// Combines portfolio chart series and monthly invested amounts
// Reuses existing DTOs to keep JSON schema consistent
data class PortfolioPerformanceResponse(
    val chart: PortfolioChartResponse,
    val monthlyInvested: MonthlyInvestedResponse
)

