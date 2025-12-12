package com.github.nenadjakic.investiq.common.dto

import java.time.LocalDate

data class PortfolioChartResponse(
    val dates: List<LocalDate>,
    val invested: List<Double>,
    val marketValue: List<Double>,
    val plPercentage: List<Double>
)