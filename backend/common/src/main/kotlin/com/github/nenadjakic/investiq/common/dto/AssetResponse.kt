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
    var companyResponse: CompanyResponse? = null
    var exchangeResponse: ExchangeResponse? = null
    if (this is Stock) {
            val company = this.company

            companyResponse = CompanyResponse(
                company.companyId!!,
                company.name,
                CountryResponse(company.country.iso2Code!!, company.country.name),
                industry = IndustryResponse(company.industry.id!!, company.industry.name)
            )
    }
    if (this is ListedAsset) {
        val exchange = this.exchange
        exchangeResponse = ExchangeResponse(
            exchange.id!!,
            exchange.mic,
            exchange.acronym,
            exchange.name,
            CountryResponse(exchange.country.iso2Code!!, exchange.country.name)
        )
    }
    return AssetResponse(
        id = this.id!!,
        symbol = this.symbol,
        company = companyResponse,
        currency = this.currency.code!!,
        exchange = exchangeResponse,
        type = this.assetType,
    )
}