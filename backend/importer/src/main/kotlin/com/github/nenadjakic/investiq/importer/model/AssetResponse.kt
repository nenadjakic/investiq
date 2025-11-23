package com.github.nenadjakic.investiq.importer.model

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.Etf
import com.github.nenadjakic.investiq.data.entity.asset.Stock
import org.hibernate.Hibernate
import java.util.UUID

data class AssetResponse (
    val id: UUID,
    val symbol: String,
    val currency: String,
    var exchange: String? = null,
)

fun Asset.toAssetResponse(): AssetResponse  {
    val real = Hibernate.unproxy(this) as Asset

    return AssetResponse(
        id = real.id!!,
        symbol = real.symbol,
        currency = real.currency.code!!,
    ).also { response ->
        when (real) {
            is Stock -> response.exchange = real.exchange?.acronym
            is Etf -> response.exchange = real.exchange?.acronym
        }
    }
}