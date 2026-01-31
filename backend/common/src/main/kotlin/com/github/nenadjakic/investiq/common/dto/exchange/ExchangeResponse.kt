package com.github.nenadjakic.investiq.common.dto.exchange

import com.github.nenadjakic.investiq.common.dto.CountryResponse
import java.util.UUID

data class ExchangeResponse (
    val id: UUID,
    val mic: String,
    val symbol: String?,
    val name: String,
    val country: CountryResponse
)