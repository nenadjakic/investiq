package com.github.nenadjakic.investiq.integration.dto

import java.math.BigDecimal
import java.time.LocalDate

data class AssetHistoryList (
    val symbol: String,
    val prices: MutableList<DatePrices> = mutableListOf()
)

data class DatePrices(
    val date: LocalDate,
    val volume: Long?,
    val open: BigDecimal?,
    val highPrice: BigDecimal?,
    val lowPrice: BigDecimal?,
    val closePrice: BigDecimal?,
    val adjustedClose: BigDecimal?
)