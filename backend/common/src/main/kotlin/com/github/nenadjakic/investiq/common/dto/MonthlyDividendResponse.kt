package com.github.nenadjakic.investiq.common.dto

import java.math.BigDecimal

data class MonthlyDividendResponse(
    val series: List<MonthlyDividendEntry>
)

data class MonthlyDividendEntry(
    val yearMonth: String, // format "YYYY-MM"
    val amount: BigDecimal
)

