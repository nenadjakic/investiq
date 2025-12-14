package com.github.nenadjakic.investiq.common.dto

import java.math.BigDecimal

data class TopBottomPerformersResponse(
    val top: List<PerformerResponse>,
    val bottom: List<PerformerResponse>
)

data class PerformerResponse(
    val asset: AssetSimpleResponse,
    val percentageChange: BigDecimal
)
