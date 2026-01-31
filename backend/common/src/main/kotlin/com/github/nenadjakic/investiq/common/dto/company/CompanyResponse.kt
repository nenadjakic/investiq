package com.github.nenadjakic.investiq.common.dto.company

import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.common.dto.industry.IndustryResponse
import java.util.UUID

data class CompanyResponse (
    val id: UUID,
    val name: String,
    val country: CountryResponse,
    val industry: IndustryResponse
)