package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.PortfolioSummaryResponse
import com.github.nenadjakic.investiq.data.repository.PortfolioRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class PortfolioService(
    private val portfolioRepository: PortfolioRepository
)  {
    fun getPortfolioSummary(): PortfolioSummaryResponse {
        val latestSnapshot = portfolioRepository.getLatestPortfolioSnapshot()
            ?: throw NoSuchElementException("No portfolio data available")

        val totalValue = latestSnapshot.totalValue
        val totalInvested = latestSnapshot.totalInvested
        val totalReturn = totalValue - totalInvested
        val totalReturnPercentage = if (totalInvested > BigDecimal.ZERO) {
            (totalReturn / totalInvested * BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        val previousSnapshot = portfolioRepository.getPreviousPortfolioSnapshot(latestSnapshot.snapshotDate)
        val todayChange = if (previousSnapshot != null) {
            totalValue - previousSnapshot.totalValue
        } else {
            BigDecimal.ZERO
        }
        val todayChangePercentage = if (previousSnapshot != null && previousSnapshot.totalValue > BigDecimal.ZERO) {
            (todayChange / previousSnapshot.totalValue * BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        return PortfolioSummaryResponse(
            snapshotDate = latestSnapshot.snapshotDate,
            totalValue = totalValue.setScale(2, RoundingMode.HALF_UP),
            totalInvested = totalInvested.setScale(2, RoundingMode.HALF_UP),
            totalReturn = totalReturn.setScale(2, RoundingMode.HALF_UP),
            totalReturnPercentage = totalReturnPercentage,
            todayChange = todayChange.setScale(2, RoundingMode.HALF_UP),
            todayChangePercentage = todayChangePercentage
        )
    }
}