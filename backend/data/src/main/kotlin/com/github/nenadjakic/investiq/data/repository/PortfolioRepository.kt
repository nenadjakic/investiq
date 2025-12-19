package com.github.nenadjakic.investiq.data.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID
import java.sql.Date

@Repository
class PortfolioRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getLatestPortfolioSnapshot(): PortfolioSnapshot? {
        val sql = """
            SELECT 
                snapshot_date,
                SUM(market_value_eur) as total_value,
                SUM(cost_basis_eur) as total_invested,
                SUM(unrealized_pl_eur) as total_unrealized_pl,
                SUM(realized_pl_eur) as total_realized_pl,
                COUNT(DISTINCT asset_id) as total_holdings,
                SUM(total_dividends_eur) as total_dividends_eur
            FROM asset_daily_snapshots
            WHERE snapshot_date = (SELECT MAX(snapshot_date) FROM asset_daily_snapshots)
            AND quantity > 0
            GROUP BY snapshot_date
        """.trimIndent()

        return jdbcTemplate.query(sql, portfolioSnapshotMapper).firstOrNull()
    }

    fun getSnapshotOnOrBefore(date: LocalDate): PortfolioSnapshot? {
        val sql = """
            SELECT 
                snapshot_date,
                SUM(market_value_eur) as total_value,
                SUM(cost_basis_eur) as total_invested,
                SUM(unrealized_pl_eur) as total_unrealized_pl,
                SUM(realized_pl_eur) as total_realized_pl,
                COUNT(DISTINCT asset_id) as total_holdings,
                SUM(total_dividends_eur) as total_dividends_eur
            FROM asset_daily_snapshots
            WHERE snapshot_date <= ?
            AND quantity > 0
            GROUP BY snapshot_date
            ORDER BY snapshot_date DESC
            LIMIT 1
        """.trimIndent()

        return jdbcTemplate.query(sql, portfolioSnapshotMapper, date).firstOrNull()
    }

    fun findDailyValuesBetween(startDate: LocalDate?, endDate: LocalDate): List<PortfolioDailyValue> {
        val fromDate: LocalDate = if (startDate != null) {
            startDate
        } else {
            val minSql = "SELECT MIN(snapshot_date) FROM asset_daily_snapshots"
            val minDate = jdbcTemplate.queryForObject(minSql, Date::class.java)
            minDate?.toLocalDate() ?: return emptyList()
        }

        if (fromDate.isAfter(endDate)) return emptyList()

        val sql = """
            SELECT snapshot_date,
                SUM(cost_basis_eur) as total_invested, 
                SUM(market_value_eur) as total_value
            FROM asset_daily_snapshots
            WHERE snapshot_date BETWEEN ? AND ?
            GROUP BY snapshot_date
            order by snapshot_date
        """.trimIndent()

        return jdbcTemplate.query(sql, portfolioDailyValueMapper, Date.valueOf(fromDate), Date.valueOf(endDate))
    }

    /**
     * Returns a list of IndustrySectorValue representing the total market value of holdings
     * grouped by industry and sector as of the latest snapshot date.
     */
    fun getValueByIndustrySector(): List<IndustrySectorValue> {
        val sql = """
            WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots)
            SELECT
              COALESCE(i.name, 'Unclassified') AS industry,
              COALESCE(sc.name, 'Unclassified') AS sector,
              SUM(COALESCE(s.market_value_eur, (s.market_price_eur * s.quantity)::numeric(36,8), 0))::numeric(36,8) AS value_eur
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            LEFT JOIN companies c ON a.company_id = c.id
            LEFT JOIN industries i ON c.industry_id = i.id
            LEFT JOIN sectors sc ON i.sector_id = sc.id
            WHERE a.asset_type = 'STOCK'
              AND s.quantity <> 0
            GROUP BY COALESCE(i.name, 'Unclassified'), COALESCE(sc.name, 'Unclassified')
            ORDER BY value_eur DESC
        """.trimIndent()

        return jdbcTemplate.query(sql, industrySectorValueMapper)
    }

    /**
     * Returns a list of CountryValue representing the total market value of holdings
     * grouped by country as of the latest snapshot date.
     */
    fun getValueByCountry(): List<CountryValue> {
        val sql = """
            WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots)
            SELECT
              COALESCE(co.name, 'Unclassified') AS country,
              SUM(COALESCE(s.market_value_eur, (s.market_price_eur * s.quantity)::numeric(36,8), 0))::numeric(36,8) AS value_eur
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            LEFT JOIN companies c ON a.company_id = c.id
            LEFT JOIN countries co ON c.country_code = co.iso2_code
            WHERE a.asset_type = 'STOCK'
              AND s.quantity <> 0
            GROUP BY COALESCE(co.name, 'Unclassified')
            ORDER BY value_eur DESC
        """.trimIndent()

        return jdbcTemplate.query(sql, countryValueMapper)
    }

    /**
     * Returns a list of CurrencyValue representing the total market value of holdings
     * grouped by asset currency as of the latest snapshot date.
     */
    fun getValueByCurrency(): List<CurrencyValue> {
        val sql = """
            WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots)
            SELECT
              COALESCE(UPPER(a.currency_code), 'UNKNOWN') AS currency,
              SUM(COALESCE(s.market_value_eur, (s.market_price_eur * s.quantity)::numeric(36,8), 0))::numeric(36,8) AS value_eur
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            WHERE s.snapshot_date = l.d
              AND s.quantity <> 0
            GROUP BY COALESCE(UPPER(a.currency_code), 'UNKNOWN')
            ORDER BY value_eur DESC
        """.trimIndent()

        return jdbcTemplate.query(sql, currencyValueMapper)
    }

    /**
     * Returns a list of AssetTypeValue representing the total market value of holdings
     * grouped by asset type as of the latest snapshot date.
     */
    fun getValueByAssetType(): List<AssetTypeValue> {
        val sql = """
            WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots)
            SELECT
              COALESCE(UPPER(a.asset_type), 'UNKNOWN') AS asset_type,
              SUM(COALESCE(s.market_value_eur, 0)) AS value_eur
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            WHERE s.snapshot_date = l.d
              AND s.quantity <> 0
            GROUP BY COALESCE(UPPER(a.asset_type), 'UNKNOWN')
            ORDER BY value_eur DESC
        """.trimIndent()

        return jdbcTemplate.query(sql, assetTypeValueMapper)
    }

    /**
     * Finds the total amount invested per month starting from the specified date.
     *
     * @param fromDate The date from which to start calculating monthly investments.
     * @return A list of MonthlyInvestedRow representing the total invested amount per month.
     */
    fun findMonthlyInvestedFrom(fromDate: LocalDate): List<MonthlyInvestedRow> {
        val sql = """
            SELECT year, month, SUM(transaction_value_eur + COALESCE(fee_amount_eur,0)) AS invested
            FROM vw_transaction_analytics
            WHERE transaction_type = 'BUY' AND transaction_date_only >= ?
            GROUP BY year, month
            ORDER BY year, month
        """.trimIndent()

        return jdbcTemplate.query(sql, monthlyInvestedRowMapper, Date.valueOf(fromDate))
    }

    /**
     * Returns the date of the first BUY transaction (transaction_date_only) or null if none.
     */
    fun getFirstInvestmentDate(): LocalDate? {
        val sql = "SELECT MIN(transaction_date_only) FROM vw_transaction_analytics WHERE transaction_type = 'BUY'"
        val date = jdbcTemplate.queryForObject(sql, Date::class.java)
        return date?.toLocalDate()
    }

    /**
     * Returns monthly invested rows for the given number of months ending this month.
     * If months is null, the range starts from the first BUY transaction date.
     * This method fills missing months with zero invested.
     */
    fun findMonthlyInvested(months: Int?): List<MonthlyInvestedRow> {
        if (months != null && months <= 0) return emptyList()

        val fromDate: LocalDate = if (months == null) {
            // start from the first investment date
            getFirstInvestmentDate() ?: return emptyList()
        } else {
            val startYM = YearMonth.now().minusMonths((months - 1).toLong())
            startYM.atDay(1)
        }

        val raw = findMonthlyInvestedFrom(fromDate)

        val startYM = YearMonth.from(fromDate)
        val actualMonths = if (months == null) {
            val nowYM = YearMonth.now()
            ((nowYM.year - startYM.year) * 12) + (nowYM.monthValue - startYM.monthValue) + 1
        } else months

        val map = raw.associateBy({ YearMonth.of(it.year, it.month) }, { it.invested })

        return (0 until actualMonths).map { offset ->
            val ym = startYM.plusMonths(offset.toLong())
            val invested = map[ym] ?: BigDecimal.ZERO
            MonthlyInvestedRow(ym.year, ym.monthValue, invested)
        }
    }

    /**
     * Finds the total dividends per month starting from the specified date.
     * @param fromDate The date from which to start calculating monthly dividends.
     */
    fun findMonthlyDividendsFrom(fromDate: LocalDate): List<MonthlyDividendRow> {
        val sql = """
            SELECT year, month, SUM(transaction_value_eur) AS amount
            FROM vw_transaction_analytics
            WHERE transaction_type = 'DIVIDEND' AND transaction_date_only >= ?
            GROUP BY year, month
            ORDER BY year, month
        """.trimIndent()

        return jdbcTemplate.query(sql, monthlyDividendRowMapper, Date.valueOf(fromDate))
    }

    /**
     * Returns the date of the first DIVIDEND transaction or null if none.
     */
    fun getFirstDividendDate(): LocalDate? {
        val sql = "SELECT MIN(transaction_date_only) FROM vw_transaction_analytics WHERE transaction_type = 'DIVIDEND'"
        val date = jdbcTemplate.queryForObject(sql, Date::class.java)
        return date?.toLocalDate()
    }

    /**
     * Returns monthly dividend rows for the given number of months ending this month.
     * If months is null, the range starts from the first DIVIDEND transaction date.
     * This method fills missing months with zero amount.
     */
    fun findMonthlyDividends(months: Int?): List<MonthlyDividendRow> {
        if (months != null && months <= 0) return emptyList()

        val fromDate: LocalDate = if (months == null) {
            getFirstDividendDate() ?: return emptyList()
        } else {
            val startYM = YearMonth.now().minusMonths((months - 1).toLong())
            startYM.atDay(1)
        }

        val raw = findMonthlyDividendsFrom(fromDate)

        val startYM = YearMonth.from(fromDate)
        val actualMonths = if (months == null) {
            val nowYM = YearMonth.now()
            ((nowYM.year - startYM.year) * 12) + (nowYM.monthValue - startYM.monthValue) + 1
        } else months

        val map = raw.associateBy({ YearMonth.of(it.year, it.month) }, { it.amount })

        return (0 until actualMonths).map { offset ->
            val ym = startYM.plusMonths(offset.toLong())
            val amount = map[ym] ?: BigDecimal.ZERO
            MonthlyDividendRow(ym.year, ym.monthValue, amount)
        }
    }

    /**
     * Returns latest asset snapshots aggregated per asset and platform (one row per asset+platform for the latest snapshot_date).
     * Aggregates quantity, market_value and cost basis and computes weighted averages for per-share fields.
     */
    fun getLatestAssetSnapshots(): List<AssetSnapshot> {
        val sql = """
            WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots)
            SELECT
              l.d AS snapshot_date,
              s.asset_id,
              SUM(s.quantity) AS quantity,
              -- weighted avg cost per share = SUM(cost_basis_eur) / SUM(quantity)
              CASE WHEN SUM(s.quantity) > 0 AND SUM(s.cost_basis_eur) IS NOT NULL
                   THEN (SUM(s.cost_basis_eur) / NULLIF(SUM(s.quantity), 0))
                   ELSE NULL
              END::numeric(36,8) AS avg_cost_per_share_eur,
              SUM(s.cost_basis_eur) AS cost_basis_eur,
              -- weighted market price = SUM(market_value_eur) / SUM(quantity) when available
              CASE WHEN SUM(s.quantity) > 0 AND SUM(s.market_value_eur) IS NOT NULL
                   THEN (SUM(s.market_value_eur) / NULLIF(SUM(s.quantity), 0))
                   ELSE NULL
              END::numeric(36,8) AS market_price_eur,
              SUM(s.market_value_eur) AS market_value_eur,
              SUM(s.unrealized_pl_eur) AS unrealized_pl_eur,
              a.symbol AS ticker,
              a.name AS name,
              a.asset_type AS asset_type
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            WHERE s.quantity <> 0
            GROUP BY l.d, s.asset_id, a.symbol, a.name, a.asset_type
            ORDER BY SUM(COALESCE(s.market_value_eur,0)) DESC
        """.trimIndent()

        return jdbcTemplate.query(sql, assetSnapshotMapper)
    }

    /**
     * Returns latest asset performances (percentage change) computed from the latest snapshot rows.
     * Percentage is computed as:
     *  - if avg_cost_per_share_eur available and > 0 and market_price_eur available: (market_price - avg_cost)/avg_cost*100
     *  - else if unrealized_pl_eur and cost_basis_eur available and cost_basis_eur > 0: unrealized_pl / cost_basis * 100
     *  - else 0
     */
    fun getLatestAssetPerformances(): List<LatestAssetPerformance> {
        val sql = """
            WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots)
            SELECT
              s.asset_id,
              a.symbol AS ticker,
              a.name AS name,
              a.asset_type AS type,
              COALESCE(a.currency_code, 'EUR') AS currency_code,
              CASE
                WHEN s.avg_cost_per_share_eur IS NOT NULL AND s.avg_cost_per_share_eur > 0 AND s.market_price_eur IS NOT NULL
                  THEN ((s.market_price_eur - s.avg_cost_per_share_eur) * 100) / s.avg_cost_per_share_eur
                WHEN s.unrealized_pl_eur IS NOT NULL AND s.cost_basis_eur IS NOT NULL AND s.cost_basis_eur > 0
                  THEN (s.unrealized_pl_eur * 100) / s.cost_basis_eur
                ELSE 0
              END::numeric(36,8) AS percentage
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            WHERE s.quantity <> 0
        """.trimIndent()

        return jdbcTemplate.query(sql, latestAssetPerformanceMapper)
    }

    /**
     * Returns dividend cost yield for each asset.
     * Dividend cost yield is calculated as: (Annualized Dividend / Cost Basis) * 100
     * Annualized dividend is calculated from all dividends since the first purchase of each asset,
     * then annualized based on the holding period.
     */
    fun getAssetDividendCostYield(): List<AssetDividendCostYield> {
        val sql = """
            WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots),
            first_buy AS (
                SELECT 
                    asset_id,
                    MIN(transaction_date_only) AS first_buy_date
                FROM vw_transaction_analytics
                WHERE transaction_type = 'BUY'
                GROUP BY asset_id
            ),
            total_dividends AS (
                SELECT 
                    t.asset_id,
                    SUM(t.transaction_value_eur) AS total_dividend_eur
                FROM vw_transaction_analytics t
                JOIN first_buy fb ON t.asset_id = fb.asset_id
                WHERE t.transaction_type = 'DIVIDEND'
                  AND t.transaction_date_only >= fb.first_buy_date
                GROUP BY t.asset_id
            )
            SELECT
              s.asset_id,
              a.symbol AS ticker,
              a.name AS name,
              COALESCE(d.total_dividend_eur, 0)::numeric(36,8) AS total_dividend_eur,
              SUM(s.cost_basis_eur)::numeric(36,8) AS cost_basis_eur,
              fb.first_buy_date,
              -- Calculate days held
              (CURRENT_DATE - fb.first_buy_date) AS days_held,
              -- Annualized dividend = total_dividend * 365 / days_held
              CASE 
                WHEN (CURRENT_DATE - fb.first_buy_date) > 0 
                  THEN (COALESCE(d.total_dividend_eur, 0) * 365.0 / (CURRENT_DATE - fb.first_buy_date))
                ELSE COALESCE(d.total_dividend_eur, 0)
              END::numeric(36,8) AS annualized_dividend_eur,
              -- Dividend cost yield = annualized_dividend / cost_basis * 100
              CASE 
                WHEN SUM(s.cost_basis_eur) > 0 AND (CURRENT_DATE - fb.first_buy_date) > 0
                  THEN ((COALESCE(d.total_dividend_eur, 0) * 365.0 / (CURRENT_DATE - fb.first_buy_date)) * 100 / SUM(s.cost_basis_eur))
                WHEN SUM(s.cost_basis_eur) > 0
                  THEN (COALESCE(d.total_dividend_eur, 0) * 100 / SUM(s.cost_basis_eur))
                ELSE 0
              END::numeric(36,8) AS dividend_cost_yield
            FROM asset_daily_snapshots s
            JOIN latest l ON s.snapshot_date = l.d
            JOIN assets a ON s.asset_id = a.id
            LEFT JOIN first_buy fb ON s.asset_id = fb.asset_id
            LEFT JOIN total_dividends d ON s.asset_id = d.asset_id
            WHERE s.quantity <> 0
            GROUP BY s.asset_id, a.symbol, a.name, d.total_dividend_eur, fb.first_buy_date
            ORDER BY dividend_cost_yield DESC
        """.trimIndent()

        return jdbcTemplate.query(sql, assetDividendCostYieldMapper)
    }

    /**
     * Returns total dividend cost yield for the entire portfolio.
     * Dividend cost yield is calculated as: (Annualized Total Dividend / Total Cost Basis) * 100
     * Annualized dividend is calculated from all dividends since the first investment,
     * then annualized based on the overall holding period.
     */
    fun getTotalDividendCostYield(): TotalDividendCostYield? {
        val sql = """
            WITH latest AS (SELECT MAX(snapshot_date) AS d FROM asset_daily_snapshots),
            first_investment AS (
                SELECT MIN(transaction_date_only) AS first_date
                FROM vw_transaction_analytics
                WHERE transaction_type = 'BUY'
            ),
            total_dividends AS (
                SELECT COALESCE(SUM(transaction_value_eur), 0) AS total_dividend_eur
                FROM vw_transaction_analytics
                WHERE transaction_type = 'DIVIDEND'
            ),
            portfolio_cost AS (
                SELECT COALESCE(SUM(s.cost_basis_eur), 0) AS total_cost_basis_eur
                FROM asset_daily_snapshots s
                JOIN latest l ON s.snapshot_date = l.d
                WHERE s.quantity <> 0
            )
            SELECT
              d.total_dividend_eur::numeric(36,8) AS total_dividend_eur,
              p.total_cost_basis_eur::numeric(36,8) AS total_cost_basis_eur,
              fi.first_date AS first_investment_date,
              COALESCE(CURRENT_DATE - fi.first_date, 0) AS days_held,
              -- Annualized dividend = total_dividend * 365 / days_held
              CASE 
                WHEN fi.first_date IS NOT NULL AND (CURRENT_DATE - fi.first_date) > 0 
                  THEN (d.total_dividend_eur * 365.0 / (CURRENT_DATE - fi.first_date))
                ELSE d.total_dividend_eur
              END::numeric(36,8) AS annualized_dividend_eur,
              -- Dividend cost yield = annualized_dividend / cost_basis * 100
              CASE 
                WHEN p.total_cost_basis_eur > 0 AND fi.first_date IS NOT NULL AND (CURRENT_DATE - fi.first_date) > 0
                  THEN ((d.total_dividend_eur * 365.0 / (CURRENT_DATE - fi.first_date)) * 100 / p.total_cost_basis_eur)
                WHEN p.total_cost_basis_eur > 0
                  THEN (d.total_dividend_eur * 100 / p.total_cost_basis_eur)
                ELSE 0
              END::numeric(36,8) AS dividend_cost_yield
            FROM total_dividends d
            CROSS JOIN portfolio_cost p
            CROSS JOIN first_investment fi
        """.trimIndent()

        return jdbcTemplate.query(sql, totalDividendCostYieldMapper).firstOrNull()
    }

    private val portfolioSnapshotMapper = RowMapper<PortfolioSnapshot> { rs, _ ->
        PortfolioSnapshot(
            snapshotDate = rs.getDate("snapshot_date").toLocalDate(),
            totalValue = rs.getBigDecimal("total_value"),
            totalInvested = rs.getBigDecimal("total_invested"),
            totalUnrealizedPL = rs.getBigDecimal("total_unrealized_pl"),
            totalRealizedPL = rs.getBigDecimal("total_realized_pl"),
            totalHoldings = rs.getInt("total_holdings"),
            totalDividends = rs.getBigDecimal("total_dividends_eur")
        )
    }

    private val portfolioDailyValueMapper = RowMapper<PortfolioDailyValue> { rs, _ ->
        PortfolioDailyValue(
            snapshotDate = rs.getDate("snapshot_date").toLocalDate(),
            totalInvested = rs.getBigDecimal("total_invested"),
            totalValue = rs.getBigDecimal("total_value")
        )
    }

    private val industrySectorValueMapper = RowMapper<IndustrySectorValue> { rs, _ ->
        IndustrySectorValue(
            industry = rs.getString("industry"),
            sector = rs.getString("sector"),
            valueEur = rs.getBigDecimal("value_eur")
        )
    }

    private val monthlyInvestedRowMapper = RowMapper<MonthlyInvestedRow> { rs, _ ->
        val year = rs.getInt("year")
        val month = rs.getInt("month")
        val invested = rs.getBigDecimal("invested") ?: BigDecimal.ZERO
        MonthlyInvestedRow(year, month, invested)
    }

    private val countryValueMapper = RowMapper<CountryValue> { rs, _ ->
        CountryValue(
            country = rs.getString("country"),
            valueEur = rs.getBigDecimal("value_eur")
        )
    }

    private val currencyValueMapper = RowMapper<CurrencyValue> { rs, _ ->
        CurrencyValue(
            currency = rs.getString("currency"),
            valueEur = rs.getBigDecimal("value_eur")
        )
    }

    private val assetTypeValueMapper = RowMapper<AssetTypeValue> { rs, _ ->
        AssetTypeValue(
            assetType = rs.getString("asset_type"),
            valueEur = rs.getBigDecimal("value_eur")
        )
    }

    private val assetSnapshotMapper = RowMapper<AssetSnapshot> { rs, _ ->
        AssetSnapshot(
            snapshotDate = rs.getDate("snapshot_date").toLocalDate(),
            assetId = rs.getObject("asset_id", UUID::class.java),
            quantity = rs.getBigDecimal("quantity"),
            avgCostPerShareEur = rs.getBigDecimal("avg_cost_per_share_eur"),
            costBasisEur = rs.getBigDecimal("cost_basis_eur"),
            marketPriceEur = rs.getBigDecimal("market_price_eur"),
            marketValueEur = rs.getBigDecimal("market_value_eur"),
            unrealizedPlEur = rs.getBigDecimal("unrealized_pl_eur"),
            ticker = rs.getString("ticker"),
            name = rs.getString("name"),
            type = rs.getString("asset_type")
        )
    }

    private val latestAssetPerformanceMapper = RowMapper<LatestAssetPerformance> { rs, _ ->
        LatestAssetPerformance(
            assetId = rs.getObject("asset_id", UUID::class.java),
            ticker = rs.getString("ticker"),
            name = rs.getString("name"),
            type = rs.getString("type"),
            percentage = rs.getBigDecimal("percentage") ?: java.math.BigDecimal.ZERO,
            currencyCode = rs.getString("currency_code")
        )
    }

    private val assetDividendCostYieldMapper = RowMapper<AssetDividendCostYield> { rs, _ ->
        AssetDividendCostYield(
            assetId = rs.getObject("asset_id", UUID::class.java),
            ticker = rs.getString("ticker"),
            name = rs.getString("name"),
            totalDividendEur = rs.getBigDecimal("total_dividend_eur") ?: BigDecimal.ZERO,
            annualizedDividendEur = rs.getBigDecimal("annualized_dividend_eur") ?: BigDecimal.ZERO,
            costBasisEur = rs.getBigDecimal("cost_basis_eur") ?: BigDecimal.ZERO,
            daysHeld = rs.getInt("days_held"),
            dividendCostYield = rs.getBigDecimal("dividend_cost_yield") ?: BigDecimal.ZERO
        )
    }

    private val totalDividendCostYieldMapper = RowMapper<TotalDividendCostYield> { rs, _ ->
        TotalDividendCostYield(
            totalDividendEur = rs.getBigDecimal("total_dividend_eur") ?: BigDecimal.ZERO,
            annualizedDividendEur = rs.getBigDecimal("annualized_dividend_eur") ?: BigDecimal.ZERO,
            totalCostBasisEur = rs.getBigDecimal("total_cost_basis_eur") ?: BigDecimal.ZERO,
            daysHeld = rs.getInt("days_held"),
            dividendCostYield = rs.getBigDecimal("dividend_cost_yield") ?: BigDecimal.ZERO
        )
    }

    private val monthlyDividendRowMapper = RowMapper<MonthlyDividendRow> { rs, _ ->
        val year = rs.getInt("year")
        val month = rs.getInt("month")
        val amount = rs.getBigDecimal("amount") ?: BigDecimal.ZERO
        MonthlyDividendRow(year, month, amount)
    }

    data class PortfolioSnapshot(
        val snapshotDate: LocalDate,
        val totalValue: BigDecimal,
        val totalInvested: BigDecimal,
        val totalUnrealizedPL: BigDecimal,
        val totalRealizedPL: BigDecimal,
        val totalHoldings: Int,
        val totalDividends: BigDecimal
    )

    data class PortfolioDailyValue(
        val snapshotDate: LocalDate,
        val totalInvested: BigDecimal,
        val totalValue: BigDecimal
    )

    data class IndustrySectorValue(
        val industry: String,
        val sector: String,
        val valueEur: BigDecimal
    )

    data class AssetSnapshot(
        val snapshotDate: LocalDate,
        val assetId: UUID,
        val quantity: BigDecimal,
        val avgCostPerShareEur: BigDecimal?,
        val costBasisEur: BigDecimal?,
        val marketPriceEur: BigDecimal?,
        val marketValueEur: BigDecimal?,
        val unrealizedPlEur: BigDecimal?,
        val ticker: String,
        val name: String,
        val type: String? = null
    )

    data class MonthlyInvestedRow(
        val year: Int,
        val month: Int,
        val invested: BigDecimal
    )

    data class CountryValue(
        val country: String,
        val valueEur: BigDecimal
    )

    data class CurrencyValue(
        val currency: String,
        val valueEur: BigDecimal
    )

    data class AssetTypeValue(
        val assetType: String,
        val valueEur: BigDecimal
    )

    data class LatestAssetPerformance(
        val assetId: UUID,
        val ticker: String,
        val name: String,
        val type: String?,
        val percentage: java.math.BigDecimal,
        val currencyCode: String?
    )

    data class MonthlyDividendRow(
        val year: Int,
        val month: Int,
        val amount: BigDecimal
    )

    data class AssetDividendCostYield(
        val assetId: UUID,
        val ticker: String,
        val name: String,
        val totalDividendEur: BigDecimal,
        val annualizedDividendEur: BigDecimal,
        val costBasisEur: BigDecimal,
        val daysHeld: Int,
        val dividendCostYield: BigDecimal
    )

    data class TotalDividendCostYield(
        val totalDividendEur: BigDecimal,
        val annualizedDividendEur: BigDecimal,
        val totalCostBasisEur: BigDecimal,
        val daysHeld: Int,
        val dividendCostYield: BigDecimal
    )
}
