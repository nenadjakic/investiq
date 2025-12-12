package com.github.nenadjakic.investiq.common.dto

import com.github.nenadjakic.investiq.data.entity.core.Currency

data class CurrencyResponse (
    val code: String,
    val symbol: String?,
    val name: String
)

fun Currency.toResponse() =
    CurrencyResponse(this.code!!, this.symbol, this.name)