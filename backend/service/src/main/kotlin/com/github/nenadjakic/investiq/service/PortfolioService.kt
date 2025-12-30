package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.AssetTypeValueResponse
import com.github.nenadjakic.investiq.common.dto.PeriodChangeResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioChartResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioSummaryResponse
import com.github.nenadjakic.investiq.common.dto.IndustrySectorValueResponse
import com.github.nenadjakic.investiq.common.dto.MonthlyInvestedEntry
import com.github.nenadjakic.investiq.common.dto.MonthlyInvestedResponse
import com.github.nenadjakic.investiq.common.dto.MonthlyDividendEntry
import com.github.nenadjakic.investiq.common.dto.MonthlyDividendResponse
import com.github.nenadjakic.investiq.common.dto.CountryValueResponse
import com.github.nenadjakic.investiq.common.dto.CurrencyValueResponse
import com.github.nenadjakic.investiq.common.dto.AssetHoldingResponse
import com.github.nenadjakic.investiq.common.dto.AssetSimpleResponse
import com.github.nenadjakic.investiq.common.dto.PerformerResponse
import com.github.nenadjakic.investiq.common.dto.TopBottomPerformersResponse
import com.github.nenadjakic.investiq.common.dto.AssetDividendCostYieldResponse
import com.github.nenadjakic.investiq.common.dto.CompanyAssetHoldingResponse
import com.github.nenadjakic.investiq.common.dto.TotalDividendCostYieldResponse
import com.github.nenadjakic.investiq.common.dto.DividendCostYieldResponse
import com.github.nenadjakic.investiq.common.dto.PortfolioConcentrationResponse
import com.github.nenadjakic.investiq.data.enum.AssetType
import com.github.nenadjakic.investiq.data.repository.PortfolioRepository
import com.github.nenadjakic.investiq.data.repository.AssetRepository
import com.github.nenadjakic.investiq.data.repository.AssetHistoryRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.YearMonth

@Service
class PortfolioService(
    private val portfolioRepository: PortfolioRepository,
    private val assetRepository: AssetRepository,
    private val assetHistoryRepository: AssetHistoryRepository
)  {

    fun getPortfolioSummary(periodDays: Int = 5): PortfolioSummaryResponse {
        val latestSnapshot = portfolioRepository.getLatestPortfolioSnapshot()
            ?: throw NoSuchElementException("No portfolio data available")

        // Use rounded values for presentation and for percentage calculations to make results deterministic
        val totalValueRaw = latestSnapshot.totalValue
        val totalInvestedRaw = latestSnapshot.totalInvested

        val totalValueRounded = totalValueRaw.setScale(2, RoundingMode.HALF_UP)
        val totalInvestedRounded = totalInvestedRaw.setScale(2, RoundingMode.HALF_UP)

        // Compute unrealized and realized P/L and use their sum as the portfolio's total P/L
        val unrealizedRounded = latestSnapshot.totalUnrealizedPL.setScale(2, RoundingMode.HALF_UP)
        val realizedRounded = latestSnapshot.totalRealizedPL.setScale(2, RoundingMode.HALF_UP)

        val totalPl = (unrealizedRounded + realizedRounded).setScale(2, RoundingMode.HALF_UP)

        val totalReturnPercentage = if (totalInvestedRounded > BigDecimal.ZERO) {
            totalPl
                .multiply(BigDecimal(100))
                .divide(totalInvestedRounded, 6, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        val periodStartDate = latestSnapshot.snapshotDate.minusDays(periodDays.toLong())
        val periodSnapshot = portfolioRepository.getSnapshotOnOrBefore(periodStartDate)

        val periodChange = calculatePeriodChange(
            latestSnapshot.snapshotDate,
            periodSnapshot?.snapshotDate ?: periodStartDate,
            totalValueRaw,
            periodSnapshot?.totalValue ?: BigDecimal.ZERO,
            periodDays
        )

        val dividendCostYield = portfolioRepository.getTotalDividendCostYield()?.dividendCostYield
            ?.setScale(2, RoundingMode.HALF_UP) ?: BigDecimal.ZERO

        return PortfolioSummaryResponse(
            snapshotDate = latestSnapshot.snapshotDate,
            totalValue = totalValueRounded,
            totalInvested = totalInvestedRounded,
            totalReturn = totalPl,
            totalReturnPercentage = totalReturnPercentage,
            totalUnrealizedPL = unrealizedRounded,
            totalRealizedPL = realizedRounded,
            totalHoldings = latestSnapshot.totalHoldings,
            periodChange = periodChange,
            totalDividends = latestSnapshot.totalDividends,
            dividendCostYield = dividendCostYield
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
        val indices = listOf("^GSPC", "^IXIC", "^STOXX50E", "^STOXX")
        val endDate = LocalDate.now()
        val startDate = if (days == null) null else endDate.minusDays(days.toLong())

        val dailyData = portfolioRepository.findDailyValuesBetween(startDate, endDate)

        if (dailyData.isEmpty()) {
            return PortfolioChartResponse(
                dates = emptyList(),
                invested = emptyList(),
                marketValue = emptyList(),
                plPercentage = emptyList(),
                indices = emptyMap()
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

        // Calculate index performance if requested
        val indicesData = calculateIndicesPerformance(indices, dates)

        return PortfolioChartResponse(
            dates = dates,
            invested = totalInvested,
            marketValue = totalValue,
            plPercentage = plPercentage,
            indices = indicesData
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

    fun getMonthlyDividends(months: Int?): MonthlyDividendResponse {
        val rows = portfolioRepository.findMonthlyDividends(months)
        val series = rows.map { row ->
            val yearMonth = YearMonth.of(row.year, row.month)
            MonthlyDividendEntry(yearMonth.toString(), row.amount)
        }

        return MonthlyDividendResponse(series)
    }

    /**
     * Returns the list of current holdings (positions) with calculated P/L and portfolio percentages.
     */
    fun getPortfolioHoldings(): List<AssetHoldingResponse> {
        val portfolioSnapshot = portfolioRepository.getLatestPortfolioSnapshot()
            ?: return emptyList()

        val assetSnapshots = portfolioRepository.getLatestAssetSnapshots()

        val totalValue = portfolioSnapshot.totalValue
        if (totalValue <= BigDecimal.ZERO) {
            return emptyList()
        }

        // Get dividend cost yield per asset and create a map by assetId
        val dividendYieldMap = portfolioRepository.getAssetDividendCostYield()
            .associateBy { it.assetId }

        return assetSnapshots.mapNotNull { snapshot ->
            if (snapshot.quantity <= BigDecimal.ZERO) {
                return@mapNotNull null
            }

            val shares = snapshot.quantity
            val currentPrice = snapshot.marketPriceEur
            val marketValue = snapshot.marketValueEur ?: BigDecimal.ZERO

            // Calculate average price with fallback
            val avgPrice =
                snapshot.avgCostPerShareEur
                    ?.setScale(2, RoundingMode.HALF_UP)
                    ?: BigDecimal.ZERO


            // Calculate P/L absolute and percentage
            val plAbsolute = snapshot.unrealizedPlEur
                ?.setScale(2, RoundingMode.HALF_UP)
                ?: BigDecimal.ZERO

            val plPercentage =
                if (snapshot.costBasisEur != null && snapshot.costBasisEur!! > BigDecimal.ZERO) {
                    (plAbsolute / snapshot.costBasisEur!! * BigDecimal(100))
                        .setScale(2, RoundingMode.HALF_UP)
                } else {
                    BigDecimal.ZERO
                }

            // Calculate portfolio percentage
            val portfolioPercentage = (marketValue / totalValue * BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP)

            // Get dividend cost yield for this asset
            val dividendCostYield = dividendYieldMap[snapshot.assetId]?.dividendCostYield
                ?.setScale(2, RoundingMode.HALF_UP) ?: BigDecimal.ZERO

            AssetHoldingResponse(
                ticker = snapshot.ticker,
                name = snapshot.name,
                shares = shares,
                avgPrice = avgPrice?.setScale(2, RoundingMode.HALF_UP) ?: BigDecimal.ZERO,
                currentPrice = currentPrice?.setScale(2, RoundingMode.HALF_UP) ?: BigDecimal.ZERO,
                profitLoss = plAbsolute,
                profitLossPercentage = plPercentage,
                portfolioPercentage = portfolioPercentage,
                platform = null,
                type = AssetType.valueOf(snapshot.type!!),
                dividendCostYield = dividendCostYield
            )
        }.sortedByDescending { it.currentPrice * it.shares }
    }

    fun getConsolidatedPortfolioHoldings(): List<CompanyAssetHoldingResponse> {
        val portfolioSnapshot = portfolioRepository.getLatestPortfolioSnapshot()
            ?: return emptyList()

        val assetSnapshots = portfolioRepository.getLatestAssetSnapshotsGroupedByCompany()

        val totalValue = portfolioSnapshot.totalValue
        if (totalValue <= BigDecimal.ZERO) {
            return emptyList()
        }

        val dividendYieldMap = portfolioRepository.getAssetDividendCostYieldGroupedByCompany()
            .associateBy { it.name }

        return assetSnapshots.map { snapshot ->
            val marketValue = snapshot.marketValueEur ?: BigDecimal.ZERO
            val profitLoss = snapshot.unrealizedPlEur?.setScale(2, RoundingMode.HALF_UP) ?: BigDecimal.ZERO
            val profitLossPercentage = if (marketValue > BigDecimal.ZERO) {
                (profitLoss / marketValue * BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
            } else BigDecimal.ZERO

            val portfolioPercentage = if (totalValue > BigDecimal.ZERO) {
                (marketValue / totalValue * BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
            } else BigDecimal.ZERO

            val dividendCostYield = dividendYieldMap[snapshot.holdingName]?.dividendCostYield
                ?.setScale(2, RoundingMode.HALF_UP) ?: BigDecimal.ZERO

            CompanyAssetHoldingResponse(
                platform = null,
                tickers = snapshot.tickers.sorted(),
                name = snapshot.holdingName,
                profitLoss = profitLoss,
                profitLossPercentage = profitLossPercentage,
                portfolioPercentage = portfolioPercentage,
                dividendCostYield = dividendCostYield
            )
        }.sortedByDescending { it.profitLoss }
    }


    /**
     * Returns active positions summary with invested amount and market value contributions.
     */
    fun getActivePositions(): List<com.github.nenadjakic.investiq.common.dto.ActivePositionResponse> {
        val portfolioSnapshot = portfolioRepository.getLatestPortfolioSnapshot()
            ?: return emptyList()

        val assetSnapshots = portfolioRepository.getLatestAssetSnapshots()

        val totalValue = portfolioSnapshot.totalValue
        val totalInvested = portfolioSnapshot.totalInvested

        if (assetSnapshots.isEmpty()) return emptyList()

        return assetSnapshots.mapNotNull { snapshot ->
            if (snapshot.quantity <= BigDecimal.ZERO) return@mapNotNull null

            val shares = snapshot.quantity
            val currentPrice = snapshot.marketPriceEur
            val marketValue = snapshot.marketValueEur ?: ((currentPrice ?: BigDecimal.ZERO) * shares)

            // Determine average price per share
            val avgPrice = when {
                snapshot.avgCostPerShareEur != null && snapshot.avgCostPerShareEur!! > BigDecimal.ZERO -> snapshot.avgCostPerShareEur!!
                snapshot.costBasisEur != null && snapshot.costBasisEur!! > BigDecimal.ZERO && shares > BigDecimal.ZERO -> snapshot.costBasisEur!!.divide(shares, 8, RoundingMode.HALF_UP)
                else -> BigDecimal.ZERO
            }

            val invested = when {
                snapshot.costBasisEur != null && snapshot.costBasisEur!! > BigDecimal.ZERO -> snapshot.costBasisEur!!.setScale(2, RoundingMode.HALF_UP)
                else -> (avgPrice * shares).setScale(2, RoundingMode.HALF_UP)
            }

            // Use multiply-then-divide with explicit scale to preserve fractional percentages
            val investedPct = if (totalInvested > BigDecimal.ZERO) {
                invested
                    .multiply(BigDecimal(100))
                    .divide(totalInvested, 8, RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP)
            } else BigDecimal.ZERO

            val plEur = (marketValue - invested).setScale(2, RoundingMode.HALF_UP)

            val plPct = if (invested > BigDecimal.ZERO) {
                plEur
                    .multiply(BigDecimal(100))
                    .divide(invested, 8, RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP)
            } else BigDecimal.ZERO

            val marketPct = if (totalValue > BigDecimal.ZERO) {
                marketValue
                    .multiply(BigDecimal(100))
                    .divide(totalValue, 8, RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP)
            } else BigDecimal.ZERO

            com.github.nenadjakic.investiq.common.dto.ActivePositionResponse(
                platform = null,
                type = AssetType.valueOf(snapshot.type!!),
                ticker = snapshot.ticker,
                name = snapshot.name,
                shares = shares,
                avgPriceEur = avgPrice.setScale(2, RoundingMode.HALF_UP),
                investedEur = invested,
                investedPercentage = investedPct,
                profitLossEur = plEur,
                profitLossPercentage = plPct,
                marketValueEur = marketValue.setScale(2, RoundingMode.HALF_UP),
                marketValuePercentage = marketPct
            )
        }.sortedByDescending { it.marketValueEur }
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

    fun getTopBottomPerformers(limit: Int = 5): TopBottomPerformersResponse {
        if (limit <= 0) {
            return TopBottomPerformersResponse(emptyList(), emptyList())
        }

        val rows = portfolioRepository.getLatestAssetPerformances()
        if (rows.isEmpty()) {
            return TopBottomPerformersResponse(emptyList(), emptyList())
        }

        val performers = rows.map { r ->
            val assetSimple = AssetSimpleResponse(
                id = r.assetId,
                symbol = r.ticker,
                name = r.name,
                type = AssetType.valueOf(r.type!!)
            )
            PerformerResponse(
                asset = assetSimple,
                percentageChange = r.percentage.setScale(2, RoundingMode.HALF_UP)
            )
        }

        val top = performers.sortedByDescending { it.percentageChange }.take(limit)
        val bottom = performers.sortedBy { it.percentageChange }.take(limit)

        return TopBottomPerformersResponse(top = top, bottom = bottom)
    }

    /**
     * Returns dividend cost yield for each asset in the portfolio.
     * Dividend cost yield = (Annualized Dividend / Cost Basis) * 100
     */
    fun getAssetDividendCostYield(): List<AssetDividendCostYieldResponse> {
        return portfolioRepository.getAssetDividendCostYield().map { row ->
            AssetDividendCostYieldResponse(
                assetId = row.assetId,
                ticker = row.ticker,
                name = row.name,
                totalDividendEur = row.totalDividendEur.setScale(2, RoundingMode.HALF_UP),
                annualizedDividendEur = row.annualizedDividendEur.setScale(2, RoundingMode.HALF_UP),
                costBasisEur = row.costBasisEur.setScale(2, RoundingMode.HALF_UP),
                daysHeld = row.daysHeld,
                dividendCostYield = row.dividendCostYield.setScale(2, RoundingMode.HALF_UP)
            )
        }
    }

    /**
     * Returns total dividend cost yield for the entire portfolio.
     * Dividend cost yield = (Annualized Total Dividend / Total Cost Basis) * 100
     */
    fun getTotalDividendCostYield(): TotalDividendCostYieldResponse? {
        val row = portfolioRepository.getTotalDividendCostYield() ?: return null
        return TotalDividendCostYieldResponse(
            totalDividendEur = row.totalDividendEur.setScale(2, RoundingMode.HALF_UP),
            annualizedDividendEur = row.annualizedDividendEur.setScale(2, RoundingMode.HALF_UP),
            totalCostBasisEur = row.totalCostBasisEur.setScale(2, RoundingMode.HALF_UP),
            daysHeld = row.daysHeld,
            dividendCostYield = row.dividendCostYield.setScale(2, RoundingMode.HALF_UP)
        )
    }

    /**
     * Returns combined dividend cost yield response with both per-asset and total portfolio yield.
     */
    fun getDividendCostYield(): DividendCostYieldResponse {
        val assets = getAssetDividendCostYield()
        val total = getTotalDividendCostYield()
        return DividendCostYieldResponse(assets = assets, total = total)
    }

    /**
     * Calculate normalized performance for requested indices.
     * Returns a map of index symbol to list of percentage changes, normalized to start at 0%.
     */
    private fun calculateIndicesPerformance(
        indexSymbols: List<String>,
        dates: List<LocalDate>
    ): Map<String, List<Double>> {
        val result = mutableMapOf<String, List<Double>>()
        
        for (symbol in indexSymbols) {
            val asset = assetRepository.findBySymbol(symbol) ?: continue
            
            // Get historical prices for the date range
            val priceMap = mutableMapOf<LocalDate, BigDecimal>()

            // Load all history once for this asset, then filter in memory per date
            val assetHistories = assetHistoryRepository.findAllByAsset_SymbolOrderByValidDate(symbol)

            for (date in dates) {
                // Find the closest historical price on or before this date from preloaded history
                val history = assetHistories
                    .filter { !it.validDate.isAfter(date) }
                    .maxByOrNull { it.validDate }
                history?.closePrice?.let { priceMap[date] = it }
            }
            
            // Calculate percentage changes normalized to start at 0%
            if (priceMap.isNotEmpty()) {
                val firstDate = dates.first()
                val firstPrice = priceMap[firstDate]
                
                if (firstPrice != null && firstPrice > BigDecimal.ZERO) {
                    val percentages = dates.map { date ->
                        val price = priceMap[date]
                        if (price != null) {
                            price.subtract(firstPrice)
                                .multiply(BigDecimal(100))
                                .divide(firstPrice, 6, RoundingMode.HALF_UP)
                                .setScale(2, RoundingMode.HALF_UP)
                                .toDouble()
                        } else {
                            Double.NaN
                        }
                    }
                    result[symbol] = percentages
                }
            }
        }
        
        return result
    }

    /**
     * Returns top concentration metrics (top1/top3/top5/top10) and HHI index for the portfolio.
     * Groups holdings by company/ETF using the repository grouped snapshot.
     * HHI is calculated using portfolio percentages expressed as percentage points and summing their squares
     * (so range is 0..10000). Returned value is scaled to 2 decimals.
     */
    fun getPortfolioConcentration(): PortfolioConcentrationResponse {
        val portfolioSnapshot = portfolioRepository.getLatestPortfolioSnapshot()
            ?: return PortfolioConcentrationResponse(
                top1 = BigDecimal.ZERO,
                top3 = BigDecimal.ZERO,
                top5 = BigDecimal.ZERO,
                top10 = BigDecimal.ZERO,
                hhi = BigDecimal.ZERO
            )

        val totalValue = portfolioSnapshot.totalValue
        if (totalValue <= BigDecimal.ZERO) {
            return PortfolioConcentrationResponse(
                top1 = BigDecimal.ZERO,
                top3 = BigDecimal.ZERO,
                top5 = BigDecimal.ZERO,
                top10 = BigDecimal.ZERO,
                hhi = BigDecimal.ZERO
            )
        }

        val grouped = portfolioRepository.getLatestAssetSnapshotsGroupedByCompany()

        // Build list of pairs (name, marketValue)
        val values = grouped.map { g ->
            val mv = g.marketValueEur ?: BigDecimal.ZERO
            Pair(g.holdingName, mv)
        }.sortedByDescending { it.second }

        // Convert to portfolio percentage (0..100) per holding
        val percentages = values.map { (_, mv) ->
            mv.multiply(BigDecimal(100))
                .divide(totalValue, 8, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP)
        }

        fun sumTop(n: Int): BigDecimal {
            return percentages.take(n).fold(BigDecimal.ZERO) { acc, v -> acc + v }
        }

        val top1 = sumTop(1)
        val top3 = sumTop(3)
        val top5 = sumTop(5)
        val top10 = sumTop(10)

        // HHI: sum of squares of portfolio percentage shares (percentage points), e.g. 50% -> 50^2 = 2500
        val hhiRaw = percentages.fold(BigDecimal.ZERO) { acc, p ->
            acc + p.multiply(p)
        }

        val hhi = hhiRaw.setScale(2, RoundingMode.HALF_UP)

        return PortfolioConcentrationResponse(
            top1 = top1.setScale(2, RoundingMode.HALF_UP),
            top3 = top3.setScale(2, RoundingMode.HALF_UP),
            top5 = top5.setScale(2, RoundingMode.HALF_UP),
            top10 = top10.setScale(2, RoundingMode.HALF_UP),
            hhi = hhi
        )
    }
}
