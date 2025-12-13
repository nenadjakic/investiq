package com.github.nenadjakic.investiq.common.dto

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.ListedAsset
import com.github.nenadjakic.investiq.data.entity.asset.Stock
import com.github.nenadjakic.investiq.data.enum.AssetType
import java.util.UUID

data class AssetResponse (
    val id: UUID,
    val type: AssetType,
    val symbol: String,
    val company: CompanyResponse?,
    val currency: String,
    var exchange: ExchangeResponse?,
)

fun Asset.toAssetResponse(): AssetResponse  {
    // One-line unwrap: try to reflectively unwrap Hibernate proxy, otherwise fall back to `this`
    val realAsset: Asset = runCatching {
        Class.forName("org.hibernate.proxy.HibernateProxy")
            .takeIf { it.isInstance(this) }
            ?.getMethod("getHibernateLazyInitializer")
            ?.invoke(this)
            ?.let { it.javaClass.getMethod("getImplementation").invoke(it) as? Asset }
    }.getOrNull() ?: this

    var companyResponse: CompanyResponse? = null
    var exchangeResponse: ExchangeResponse? = null

    if (realAsset is Stock) {
        val company = realAsset.company

        companyResponse = CompanyResponse(
            company.companyId!!,
            company.name,
            CountryResponse(company.country.iso2Code!!, company.country.name),
            industry = IndustryResponse(company.industry.id!!, company.industry.name)
        )
    }
    if (realAsset is ListedAsset) {
        val exchange = realAsset.exchange
        exchangeResponse = ExchangeResponse(
            exchange.id!!,
            exchange.mic,
            exchange.acronym,
            exchange.name,
            CountryResponse(exchange.country.iso2Code!!, exchange.country.name)
        )
    }
    return AssetResponse(
        id = realAsset.id!!,
        symbol = realAsset.symbol,
        company = companyResponse,
        currency = realAsset.currency.code!!,
        exchange = exchangeResponse,
        type = realAsset.assetType,
    )
}