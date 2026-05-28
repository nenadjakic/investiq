package com.github.nenadjakic.investiq.common.dto

data class MonthlyPlResponse(
    val series: Map<Int, List<MonthlyPlEntry>>  // year -> entries
)

data class MonthlyPlEntry(
    val month: Int,  // 1-12
    val plPercent: Double
)