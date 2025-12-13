package com.github.nenadjakic.investiq.common.dto

import java.math.BigDecimal

data class MonthlyInvestedResponse(
    val series: List<MonthlyInvestedEntry>
)

data class MonthlyInvestedEntry(
    val yearMonth: String, // format "YYYY-MM"
    val invested: BigDecimal
)
