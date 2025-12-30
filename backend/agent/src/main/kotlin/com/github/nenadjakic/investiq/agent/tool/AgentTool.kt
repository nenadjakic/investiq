package com.github.nenadjakic.investiq.agent.tool

import com.github.nenadjakic.investiq.common.dto.AssetHoldingResponse
import com.github.nenadjakic.investiq.common.dto.IndustrySectorValueResponse
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

    @Tool(description = "Get current portfolio holdings with ticker symbols and current values")
    fun getPortfolioHoldings(): String =
        toonSerializer.serialize(getPortfolioHoldingsRaw())

    @Tool(description = "Get portfolio allocation by sector, including total value and percentage of the portfolio")
    fun getSectorAllocation(): String {
       return toonSerializer.serialize(getSectorAllocationRaw())
    }

    @Tool(description = "Get portfolio allocation by industry and sector")
    fun getIndustrySectorValues(): String {
        return toonSerializer.serialize(getIndustrySectorValuesRaw())
    }

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

    data class SectorAllocationResponse(
        val sector: String,
        val totalValueEur: BigDecimal,
        val percentage: BigDecimal
    )
}