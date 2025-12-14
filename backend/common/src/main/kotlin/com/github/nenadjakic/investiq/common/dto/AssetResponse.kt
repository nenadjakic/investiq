package com.github.nenadjakic.investiq.common.dto

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.ListedAsset
import com.github.nenadjakic.investiq.data.entity.asset.Stock
import com.github.nenadjakic.investiq.data.enum.AssetType
import com.github.nenadjakic.investiq.data.enum.Platform
import java.util.UUID

data class AssetResponse(
    val id: UUID,
    val type: AssetType,
    val symbol: String,
    val company: CompanyResponse?,
    val currency: String,
    var exchange: ExchangeResponse?,
    var aliases: List<AssetAliasResponse>? = null
)

data class AssetSimpleResponse(
    val id: UUID,
    val symbol: String,
    val name: String,
    val type: AssetType
)

data class AssetAliasResponse(
    val id: UUID,
    val platform: Platform,
    val symbol: String
)

fun Asset.toAssetResponse(): AssetResponse {

    var companyResponse: CompanyResponse? = null
    var exchangeResponse: ExchangeResponse? = null

    if (this is Stock) {
        val company = this.company

        companyResponse = CompanyResponse(
            company.id!!,
            company.name,
            CountryResponse(company.country.iso2Code!!, company.country.name),
            industry = IndustryResponse(
                company.industry.id!!,
                company.industry.name,
                SectorSimpleResponse(company.industry.sector.id!!, company.industry.sector.name)
            )
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
        aliases = this.aliases.map {
            AssetAliasResponse(
                id = it.id!!,
                platform = it.platform,
                symbol = it.externalSymbol
            )
        }
    )
}