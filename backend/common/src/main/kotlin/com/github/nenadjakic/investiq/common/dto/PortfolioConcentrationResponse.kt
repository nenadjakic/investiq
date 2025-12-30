package com.github.nenadjakic.investiq.common.dto

import java.math.BigDecimal
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Portfolio concentration metrics and HHI")
data class PortfolioConcentrationResponse(
    @param:Schema(description = "Top 1 company share percentage in portfolio", example = "35.50")
    val top1: BigDecimal,

    @param:Schema(description = "Top 3 companies combined share percentage in portfolio", example = "65.10")
    val top3: BigDecimal,

    @param:Schema(description = "Top 5 companies combined share percentage in portfolio", example = "78.20")
    val top5: BigDecimal,

    @param:Schema(description = "Top 10 companies combined share percentage in portfolio", example = "92.30")
    val top10: BigDecimal,

    @param:Schema(description = "Herfindahl–Hirschman Index (HHI) expressed using percentage points squared (0-10000). For example, two firms with 50% each => 5000")
    val hhi: BigDecimal
)

