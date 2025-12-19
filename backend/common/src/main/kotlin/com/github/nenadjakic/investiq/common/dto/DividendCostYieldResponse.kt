package com.github.nenadjakic.investiq.common.dto

import java.math.BigDecimal
import java.util.UUID

/**
 * Response for dividend cost yield per asset.
 */
data class AssetDividendCostYieldResponse(
    val assetId: UUID,
    val ticker: String,
    val name: String,
    val totalDividendEur: BigDecimal,
    val annualizedDividendEur: BigDecimal,
    val costBasisEur: BigDecimal,
    val daysHeld: Int,
    val dividendCostYield: BigDecimal
)

/**
 * Response for total portfolio dividend cost yield.
 */
data class TotalDividendCostYieldResponse(
    val totalDividendEur: BigDecimal,
    val annualizedDividendEur: BigDecimal,
    val totalCostBasisEur: BigDecimal,
    val daysHeld: Int,
    val dividendCostYield: BigDecimal
)

/**
 * Combined response containing both per-asset and total dividend cost yield.
 */
data class DividendCostYieldResponse(
    val assets: List<AssetDividendCostYieldResponse>,
    val total: TotalDividendCostYieldResponse?
)

