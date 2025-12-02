package com.github.nenadjakic.investiq.commonservice.model

import com.github.nenadjakic.investiq.data.entity.history.CurrencyHistory
import java.math.BigDecimal
import java.time.LocalDate

data class CurrencyHistoryRequest(
    val fromCurrency: String,
    val toCurrency: String,
    val date: LocalDate,
    val exchangeRate: BigDecimal
)