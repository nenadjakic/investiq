package com.github.nenadjakic.investiq.common.dto

import com.github.nenadjakic.investiq.common.dto.industry.IndustrySimpleResponse
import java.util.UUID

data class SectorResponse(
    val id: UUID,
    val name: String,
    val industries: List<IndustrySimpleResponse>
)

data class SectorSimpleResponse(
    val id: UUID,
    val name: String
)