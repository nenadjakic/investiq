package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.AssetTypeValueResponse
import com.github.nenadjakic.investiq.common.dto.PeriodChangeResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioChartResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioSummaryResponse
import com.github.nenadjakic.investiq.common.dto.IndustrySectorValueResponse
import com.github.nenadjakic.investiq.common.dto.MonthlyInvestedEntry
import com.github.nenadjakic.investiq.common.dto.MonthlyInvestedResponse
import com.github.nenadjakic.investiq.common.dto.CountryValueResponse
import com.github.nenadjakic.investiq.common.dto.CurrencyValueResponse
import com.github.nenadjakic.investiq.data.repository.PortfolioRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.YearMonth

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

    fun getIndustrySectorAllocation(): List<IndustrySectorValueResponse> {
        val rows = portfolioRepository.getValueByIndustrySector()
        return rows.map { r ->
            IndustrySectorValueResponse(
                industry = r.industry,
                sector = r.sector,
                valueEur = r.valueEur
            )
        }
    }

    fun getCountryAllocation(): List<CountryValueResponse> {
        val rows = portfolioRepository.getValueByCountry()
        return rows.map { r ->
            CountryValueResponse(
                country = r.country,
                valueEur = r.valueEur
            )
        }
    }

    fun getCurrencyExposure(): List<CurrencyValueResponse> {
        val rows = portfolioRepository.getValueByCurrency()
        return rows.map { r ->
            CurrencyValueResponse(
                currency = r.currency,
                valueEur = r.valueEur
            )
        }
    }

    fun getAssetTypeAllocation(): List<AssetTypeValueResponse> {
        val rows = portfolioRepository.getValueByAssetType()
        return rows.map { r ->
            AssetTypeValueResponse(
                assetType = r.assetType,
                valueEur = r.valueEur
            )
        }
    }

    fun getPortfolioValueSeries(days: Int?): PortfolioChartResponse {
        val endDate = LocalDate.now()
        val startDate = if (days == null) null else endDate.minusDays(days.toLong())

        val dailyData = portfolioRepository.findDailyValuesBetween(startDate, endDate)

        if (dailyData.isEmpty()) {
            return PortfolioChartResponse(
                dates = emptyList(),
                invested = emptyList(),
                marketValue = emptyList(),
                plPercentage = emptyList()
            )
        }

        val dates = dailyData.map { it.snapshotDate }
        val totalValue = dailyData.map { it.totalValue.setScale(2, RoundingMode.HALF_UP).toDouble() }
        val totalInvested = dailyData.map { it.totalInvested.setScale(2, RoundingMode.HALF_UP).toDouble() }

        // Calculate PL% per day
        val rawPlPercentage = dailyData.map { dv ->
            val investedBD = dv.totalInvested
            val valueBD = dv.totalValue
            if (investedBD > BigDecimal.ZERO) {
                valueBD
                    .subtract(investedBD)
                    .multiply(BigDecimal(100))
                    .divide(investedBD, 6, RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP)
                    .toDouble()
            } else {
                0.0
            }
        }

        // Normalize to start at 0% - subtract the first day's PL%
        val firstDayPL = rawPlPercentage.firstOrNull() ?: 0.0
        val plPercentage = rawPlPercentage.map { it - firstDayPL }

        return PortfolioChartResponse(
            dates = dates,
            invested = totalInvested,
            marketValue = totalValue,
            plPercentage = plPercentage
        )
    }

    fun getMonthlyInvested(months: Int?): MonthlyInvestedResponse {
         val series = portfolioRepository.findMonthlyInvested(months)
            .map {
                val yearMonth = YearMonth.of(it.year, it.month)
            MonthlyInvestedEntry(yearMonth.toString(), it.invested)
        }

        return MonthlyInvestedResponse(series)
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

        val periodLabel = generatePeriodLabel(requestedDays = requestedPeriodDays)

        return PeriodChangeResponse(
            startDate = startDate,
            endDate = endDate,
            periodDays = actualPeriodDays,
            changeAmount = changeAmount.setScale(2, RoundingMode.HALF_UP),
            changePercentage = changePercentage,
            periodLabel = periodLabel
        )
    }

    private fun generatePeriodLabel(requestedDays: Int): String {
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