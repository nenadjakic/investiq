package com.github.nenadjakic.investiq.common.dto

import java.math.BigDecimal

data class AssetTypeValueResponse(
    val assetType: String,
    val valueEur: BigDecimal
)

