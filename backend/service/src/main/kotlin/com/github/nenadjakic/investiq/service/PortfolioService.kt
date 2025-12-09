package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.PeriodChangeResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioSummaryResponse
import com.github.nenadjakic.investiq.data.repository.PortfolioRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class PortfolioService(
    private val portfolioRepository: PortfolioRepository
)  {

    fun getPortfolioSummary(periodDays: Int = 5): PortfolioSummaryResponse {
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

        // Calculate period change
        val periodStartDate = latestSnapshot.snapshotDate.minusDays(periodDays.toLong())
        val periodSnapshot = portfolioRepository.getSnapshotOnOrBefore(periodStartDate)

        val periodChange = calculatePeriodChange(
            latestSnapshot.snapshotDate,
            periodSnapshot?.snapshotDate ?: periodStartDate,
            totalValue,
            periodSnapshot?.totalValue ?: BigDecimal.ZERO,
            periodDays
        )

        return PortfolioSummaryResponse(
            snapshotDate = latestSnapshot.snapshotDate,
            totalValue = totalValue.setScale(2, RoundingMode.HALF_UP),
            totalInvested = totalInvested.setScale(2, RoundingMode.HALF_UP),
            totalReturn = totalReturn.setScale(2, RoundingMode.HALF_UP),
            totalReturnPercentage = totalReturnPercentage,
            totalUnrealizedPL = latestSnapshot.totalUnrealizedPL.setScale(2, RoundingMode.HALF_UP),
            totalRealizedPL = latestSnapshot.totalRealizedPL.setScale(2, RoundingMode.HALF_UP),
            totalHoldings = latestSnapshot.totalHoldings,
            periodChange = periodChange,
            totalDividends = latestSnapshot.totalDividends
        )
    }

    private fun calculatePeriodChange(
        endDate: LocalDate,
        startDate: LocalDate,
        currentValue: BigDecimal,
        previousValue: BigDecimal,
        requestedPeriodDays: Int
    ): PeriodChangeResponse {
        val actualPeriodDays = ChronoUnit.DAYS.between(startDate, endDate).toInt()

        val changeAmount = if (previousValue > BigDecimal.ZERO) {
            currentValue - previousValue
        } else {
            BigDecimal.ZERO
        }

        val changePercentage = if (previousValue > BigDecimal.ZERO) {
            (changeAmount / previousValue * BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        val periodLabel = generatePeriodLabel(requestedPeriodDays, actualPeriodDays)

        return PeriodChangeResponse(
            startDate = startDate,
            endDate = endDate,
            periodDays = actualPeriodDays,
            changeAmount = changeAmount.setScale(2, RoundingMode.HALF_UP),
            changePercentage = changePercentage,
            periodLabel = periodLabel
        )
    }

    private fun generatePeriodLabel(requestedDays: Int, actualDays: Int): String {
        return when {
            requestedDays == 1 -> "today"
            requestedDays == 7 -> "last week"
            requestedDays == 30 -> "last month"
            requestedDays == 90 -> "last 3 months"
            requestedDays == 365 -> "last year"
            requestedDays < 7 -> "last $requestedDays days"
            requestedDays % 7 == 0 && requestedDays < 30 -> {
                val weeks = requestedDays / 7
                "last $weeks weeks"
            }
            requestedDays % 30 == 0 && requestedDays < 365 -> {
                val months = requestedDays / 30
                "last $months months"
            }
            else -> "last $requestedDays days"
        }
    }
}