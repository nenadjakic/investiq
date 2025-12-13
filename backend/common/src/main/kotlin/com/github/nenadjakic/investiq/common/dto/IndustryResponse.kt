package com.github.nenadjakic.investiq.common.dto

import java.util.UUID

data class IndustryResponse(
    val id: UUID,
    val name: String,
    val sector: SectorSimpleResponse
)

data class IndustrySimpleResponse(
    val id: UUID,
    val name: String
)