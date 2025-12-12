package com.github.nenadjakic.investiq.common.dto

import java.math.BigDecimal

data class IndustrySectorValueResponse(
    val industry: String,
    val sector: String,
    val valueEur: BigDecimal
)

