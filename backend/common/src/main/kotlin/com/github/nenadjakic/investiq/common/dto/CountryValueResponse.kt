package com.github.nenadjakic.investiq.common.dto

import java.math.BigDecimal

data class CountryValueResponse(
    val country: String,
    val valueEur: BigDecimal
)

