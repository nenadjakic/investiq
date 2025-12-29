package com.github.nenadjakic.investiq.agent.tool

import com.github.nenadjakic.investiq.service.PortfolioService
import com.github.nenadjakic.toon.serializer.ReflectionToonSerializer
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service


@Service
class AgentTool(
    private val portfolioService: PortfolioService,
    private val toonSerializer: ReflectionToonSerializer
) {

    @Tool(description = "Get current portfolio holdings with ticker symbols and current values")
    fun getPortfolioHoldings(): String =
        toonSerializer.serialize(portfolioService.getPortfolioHoldings())

}