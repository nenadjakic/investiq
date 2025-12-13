package com.github.nenadjakic.investiq.common.dto

import java.math.BigDecimal

data class CurrencyValueResponse(
    val currency: String,
    val valueEur: BigDecimal
)

