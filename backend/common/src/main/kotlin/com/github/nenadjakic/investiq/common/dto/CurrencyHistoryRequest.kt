package com.github.nenadjakic.investiq.common.dto

import java.math.BigDecimal
import java.time.LocalDate

data class CurrencyHistoryRequest(
    val fromCurrency: String,
    val toCurrency: String,
    val date: LocalDate,
    val exchangeRate: BigDecimal
)