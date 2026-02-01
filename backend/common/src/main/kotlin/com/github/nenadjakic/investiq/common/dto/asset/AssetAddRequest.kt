package com.github.nenadjakic.investiq.common.dto.asset

import com.github.nenadjakic.investiq.data.enum.AssetClass
import com.github.nenadjakic.investiq.data.enum.AssetType
import com.github.nenadjakic.investiq.data.enum.Platform
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AssetAddRequest (

    @field:NotNull(message = "Asset type is required")
    val assetType: AssetType?,

    @field:NotEmpty(message = "Symbol is required")
    val symbol: String?,

    @field:NotEmpty(message = "Name is required")
    val name: String?,

    @field:NotEmpty(message = "Currency code is required")
    val currencyCode: String?,

    val exchangeId: UUID?,

    val companyId: UUID?,

    val fundManager: String?,

    val assetClass: AssetClass?,

    val trackedIndexId: UUID?,

    val aliases: Map<Platform, String>?
)