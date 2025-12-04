package com.github.nenadjakic.investiq.common.dto

import java.util.UUID

data class CompanyResponse (
    val id: UUID,
    val name: String,
    val country: CountryResponse,
    val industry: IndustryResponse
)