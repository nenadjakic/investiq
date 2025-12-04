package com.github.nenadjakic.investiq.integration.dto

import java.math.BigDecimal
import java.time.LocalDate

data class CurrencyHistoryResponse (
    val fromCurrency: String,
    val toCurrency: String,
    val date: LocalDate,
    val exchangeRate: BigDecimal
)