package com.github.nenadjakic.investiq.common.dto

import com.github.nenadjakic.investiq.data.entity.core.Currency
import jakarta.xml.bind.Binder
import java.math.BigDecimal

data class CurrencyResponse (
    val code: String,
    val symbol: String?,
    val name: String,
    val parent: CurrencySimpleResponse?,
    val parentFactor: BigDecimal?
)
data class CurrencySimpleResponse (
    val code: String,
    val symbol: String?,
    val name: String
)

fun Currency.toResponse(): CurrencyResponse {
    val parent = if (this.parent != null) {
        CurrencySimpleResponse(
            this.parent!!.code!!,
            this.parent!!.symbol,
            this.parent!!.name
        )
    } else {
        null
    }

    return CurrencyResponse(
        this.code!!,
        this.symbol,
        this.name,
        parent,
        this.toParentFactor
    )
}