package com.github.nenadjakic.investiq.common.extension

import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.common.dto.exchange.ExchangeResponse
import com.github.nenadjakic.investiq.data.entity.core.Exchange

fun Exchange.toExchangeResponse(): ExchangeResponse =
    ExchangeResponse(
        id = this.id!!,
        mic = this.mic,
        symbol = this.acronym,
        name = this.name,
        country = CountryResponse(
            code = this.country.iso2Code!!,
            name = this.country.name
        )
    )