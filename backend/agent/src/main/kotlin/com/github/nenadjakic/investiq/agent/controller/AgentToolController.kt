package com.github.nenadjakic.investiq.agent.controller

import com.github.nenadjakic.investiq.agent.tool.AgentTool
import com.github.nenadjakic.investiq.agent.tool.AgentTool.SectorAllocationResponse
import com.github.nenadjakic.investiq.common.dto.AssetHoldingResponse
import com.github.nenadjakic.investiq.common.dto.IndustrySectorValueResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tool")
class AgentToolController(
    private val agentTool: AgentTool
) {

    @GetMapping(value = ["sector-allocation"], produces = ["application/toon"])
    fun getSectorAllocation(): ResponseEntity<List<SectorAllocationResponse>> {
        return ResponseEntity.ok(agentTool.getSectorAllocationRaw())
    }

    @GetMapping(value = ["industry-allocation"], produces = ["application/toon"])
    fun getIndustryAllocation(): ResponseEntity<List<IndustrySectorValueResponse>> {
        return ResponseEntity.ok(agentTool.getIndustrySectorValuesRaw())
    }

    @GetMapping(value= ["holdings"], produces = ["application/toon"])
    fun getHoldings(): ResponseEntity<List<AssetHoldingResponse>> {
        return ResponseEntity.ok(agentTool.getPortfolioHoldingsRaw())
    }
}