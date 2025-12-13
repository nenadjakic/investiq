package com.github.nenadjakic.investiq.common.dto

data class PortfolioAllocationResponse(
    val byCurrency: List<CurrencyValueResponse>,
    val byIndustrySector: List<IndustrySectorValueResponse>,
    val byCountry: List<CountryValueResponse>,
    val byAssetType: List<AssetTypeValueResponse>
)
