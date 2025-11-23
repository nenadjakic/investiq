package com.github.nenadjakic.investiq.importer.model

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.Etf
import com.github.nenadjakic.investiq.data.entity.asset.Stock

data class AssetResponse (
    val symbol: String,
    val currency: String,
    var exchange: String? = null,
)

fun Asset.toAssetResponse(): AssetResponse =
    AssetResponse(
        symbol = this.symbol,
        currency = this.currency.code!!,
    ).also {
        if (this is Stock || this is Etf) {
            it.exchange = this.exchange?.acronym
        }
    }