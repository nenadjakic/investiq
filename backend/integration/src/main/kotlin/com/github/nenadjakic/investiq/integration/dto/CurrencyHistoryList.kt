package com.github.nenadjakic.investiq.integration.dto

import java.math.BigDecimal
import java.time.LocalDate

data class CurrencyHistoryList (
    val fromCurrency: String,
    val toCurrency: String,
    val exchangeRates: MutableList<DateExchangeRate> = mutableListOf()
)

data class DateExchangeRate(
    val date: LocalDate,
    val exchangeRate: BigDecimal
)