package com.github.nenadjakic.investiq.common.dto

// Combines allocations by currency, industry/sector and country
// Reuses existing response DTOs for consistency
data class PortfolioAllocationResponse(
    val byCurrency: List<CurrencyValueResponse>,
    val byIndustrySector: List<IndustrySectorValueResponse>,
    val byCountry: List<CountryValueResponse>,
    val byAssetType: List<AssetTypeValueResponse>
)
