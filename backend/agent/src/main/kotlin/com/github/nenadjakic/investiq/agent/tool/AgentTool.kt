package com.github.nenadjakic.investiq.agent.tool

import com.github.nenadjakic.investiq.common.dto.AssetHoldingResponse
import com.github.nenadjakic.investiq.common.dto.IndustrySectorValueResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioConcentrationResponse
import com.github.nenadjakic.investiq.service.PortfolioService
import com.github.nenadjakic.toon.serializer.ReflectionToonSerializer
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode


@Service
class AgentTool(
    private val portfolioService: PortfolioService,
    private val toonSerializer: ReflectionToonSerializer
) {

    @Tool(description = """
Get current portfolio holdings with ticker symbols, types, shares, and current values. 
Only returns actual holdings; do not assume missing data. This is the source-of-truth for individual position details.
    """)
    fun getPortfolioHoldings(): String =
        toonSerializer.serialize(getPortfolioHoldingsRaw())

    @Tool(description = """
Get portfolio allocation aggregated by sector, including total value in EUR and percentage of the total portfolio. 
Use primarily for initial analysis of diversification and sector-level concentration.
    """)
    fun getSectorAllocation(): String {
       return toonSerializer.serialize(getSectorAllocationRaw())
    }

    @Tool(description = """
Get portfolio allocation by industry within each sector (absolute values). 
Use only for detailed drill-down analysis; do not use for overall sector concentration.
    """)
    fun getIndustrySectorValues(): String {
        return toonSerializer.serialize(getIndustrySectorValuesRaw())
    }

    @Tool(description = """
Get portfolio concentration metrics, including share of top holdings (top1, top3, top5, top10) and Herfindahl-Hirschman Index (HHI). 
ETF holdings are treated as single positions; underlying company allocation is not assumed. Metrics are descriptive only.
    """)
    fun getPortfolioConcentration(): String =
        toonSerializer.serialize(getPortfolioConcentrationRaw())

    @Tool(description = """
Get portfolio allocation by geographic region, including percentage of total portfolio. 
Use for analyzing regional concentration and geographic diversification.
    """)
    fun getCountryAllocation(): String =
        toonSerializer.serialize(getCountryAllocationRaw())

    @Tool(description = """
Get portfolio allocation by asset class (Equity, Bonds, Commodities, Cash), including percentage of total portfolio. 
Use for understanding structural portfolio risk.
    """)
    fun getAssetTypeAllocation(): String =
        toonSerializer.serialize(getAssetTypeAllocationRaw())

    fun getPortfolioHoldingsRaw(): List<AssetHoldingResponse> =
        portfolioService.getPortfolioHoldings()

    fun getSectorAllocationRaw(): List<SectorAllocationResponse> {
        val response = portfolioService.getIndustrySectorAllocation()
        val totalPortfolioValue = response.sumOf { it.valueEur }

        response.groupBy { it.sector }
            .map { (sector, values) ->
                val sectorTotal = values.fold(BigDecimal.ZERO) { acc, r -> acc + r.valueEur }
                SectorAllocationResponse(
                    sector,
                    sectorTotal,
                    sectorTotal
                        .divide(totalPortfolioValue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(100))
                )
            }.let {
                return it
            }
    }

    fun getIndustrySectorValuesRaw(): List<IndustrySectorValueResponse> {
        return portfolioService.getIndustrySectorAllocation()
    }

    fun getPortfolioConcentrationRaw(): PortfolioConcentrationResponse =
        portfolioService.getPortfolioConcentration()

    fun getCountryAllocationRaw(): List<CountryAllocationResponse> {
        val response = portfolioService.getCountryAllocation()
        val totalPortfolioValue = response.sumOf { it.valueEur }

        return response.map { countryValue ->
            CountryAllocationResponse(
                countryValue.country,
                countryValue.valueEur,
                countryValue.valueEur
                    .divide(totalPortfolioValue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            )
        }
    }

    fun getAssetTypeAllocationRaw(): List<AssetTypeAllocationResponse> {
        val response = portfolioService.getAssetTypeAllocation()
        val totalPortfolioValue = response.sumOf { it.valueEur }

        return response.map { assetTypeValue ->
            AssetTypeAllocationResponse(
                assetTypeValue.assetType,
                assetTypeValue.valueEur,
                assetTypeValue.valueEur
                    .divide(totalPortfolioValue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            )
        }
    }

    data class SectorAllocationResponse(
        val sector: String,
        val totalValueEur: BigDecimal,
        val percentage: BigDecimal
    )

    data class CountryAllocationResponse(
        val country: String,
        val totalValueEur: BigDecimal,
        val percentage: BigDecimal
    )

    data class AssetTypeAllocationResponse(
        val assetClass: String,
        val totalValueEur: BigDecimal,
        val percentage: BigDecimal
    )
}