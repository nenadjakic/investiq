package com.github.nenadjakic.investiq.commonservice.model

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.Etf
import com.github.nenadjakic.investiq.data.entity.asset.Stock
import java.util.UUID

data class AssetResponse (
    val id: UUID,
    val symbol: String,
    val currency: String,
    var exchange: String? = null,
)

fun Asset.toAssetResponse(): AssetResponse  {
    return AssetResponse(
        id = this.id!!,
        symbol = this.symbol,
        currency = this.currency.code!!,
    ).also { response ->
        when (this) {
            is Stock -> response.exchange = this.exchange.acronym
            is Etf -> response.exchange = this.exchange.acronym
        }
    }
}