package com.github.nenadjakic.investiq.data.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID
import java.sql.Date
import com.github.nenadjakic.investiq.data.enum.Platform

@Repository
class PortfolioRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun getLatestPortfolioSnapshot(platform: Platform? = null): PortfolioSnapshot? {
        val sql = """
            SELECT 
                snapshot_date,
                total_value,
                total_invested,
                total_unrealized_pl,
                total_realized_pl,
                total_holdings,
                total_dividends_eur
            FROM public.get_latest_portfolio_snapshot(?)
        """.trimIndent()

        return jdbcTemplate.query(sql, portfolioSnapshotMapper, platform?.name).firstOrNull()
    }

    fun getSnapshotOnOrBefore(date: LocalDate, platform: Platform? = null): PortfolioSnapshot? {
        val sql = """
            select 
                snapshot_date,
                total_value,
                total_invested,
                total_unrealized_pl,
                total_realized_pl,
                total_holdings,
                total_dividends_eur
            from public.get_portfolio_snapshot_at_date_or_before(?, ?)
        """.trimIndent()

        return jdbcTemplate.query(sql, portfolioSnapshotMapper, date, platform?.name).firstOrNull()
    }

    fun findDailyValuesBetween(
        startDate: LocalDate?,
        endDate: LocalDate,
        platform: Platform? = null
    ): List<PortfolioDailyValue> {
        val fromDate: LocalDate = if (startDate != null) {
            startDate
        } else {
            val minSql = "SELECT MIN(snapshot_date) FROM asset_daily_snapshots"
            val minDate = jdbcTemplate.queryForObject(minSql, Date::class.java)
            minDate?.toLocalDate() ?: return emptyList()
        }

        if (fromDate.isAfter(endDate)) return emptyList()

        val sql = StringBuilder(
            """
            SELECT snapshot_date,
                   SUM(cost_basis_eur) AS total_invested,
                   SUM(market_value_eur) AS total_value
            FROM asset_daily_snapshots
            WHERE snapshot_date BETWEEN ? AND ?
        """
        )

        val params = mutableListOf<Any>(
            Date.valueOf(fromDate),
            Date.valueOf(endDate)
        )

        if (platform != null) {
            sql.append(" AND platform = ?")
            params.add(platform.name)
        }

        sql.append(" GROUP BY snapshot_date ORDER BY snapshot_date")

        return jdbcTemplate.query(sql.toString(), portfolioDailyValueMapper, *params.toTypedArray())
    }

    /**
     * Returns a list of IndustrySectorValue representing the total market value of holdings
     * grouped by industry and sector as of the latest snapshot date.
     */
    fun getValueByIndustrySector(platform: Platform? = null): List<IndustrySectorValue> {
        val sql = """
            select 
                industry,
                sector,
                value_eur
            from public.get_portfolio_sector_industry_allocation(?)
        """.trimIndent()

        return jdbcTemplate.query(sql, industrySectorValueMapper, platform?.name)
    }

    /**
     * Returns a list of CountryValue representing the total market value of holdings
     * grouped by country as of the latest snapshot date.
     */
    fun getValueByCountry(platform: Platform? = null): List<CountryValue> {
        val sql = """
            select
                country,
                value_eur
            from public.get_portfolio_country_allocation(?)
        """.trimIndent()

        return jdbcTemplate.query(sql, countryValueMapper, platform?.name)
    }

    /**
     * Returns a list of CurrencyValue representing the total market value of holdings
     * grouped by asset currency as of the latest snapshot date.
     */
    fun getValueByCurrency(platform: Platform? = null): List<CurrencyValue> {
        val sql = """
            select
                currency,
                value_eur
            from public.get_portfolio_currency_allocation(?)
        """.trimIndent()

        return jdbcTemplate.query(sql, currencyValueMapper, platform?.name)
    }

    /**
     * Returns a list of AssetTypeValue representing the total market value of holdings
     * grouped by asset type as of the latest snapshot date.
     */
    fun getValueByAssetType(platform: Platform? = null): List<AssetTypeValue> {
        val sql = """
            select
                asset_type,
                value_eur
            from public.get_portfolio_asset_type_allocation(?)
        """.trimIndent()

        return jdbcTemplate.query(sql, assetTypeValueMapper, platform?.name)
    }

    /**
     * Finds the total amount invested per month starting from the specified date.
     *
     * @param fromDate The date from which to start calculating monthly investments.
     * @return A list of MonthlyInvestedRow representing the total invested amount per month.
     */
    fun findMonthlyInvestedFrom(fromDate: LocalDate, platform: Platform? = null): List<MonthlyInvestedRow> {
        val sql = StringBuilder("""
            SELECT 
                year, 
                month, 
                SUM(transaction_value_eur + COALESCE(fee_amount_eur, 0)) AS invested
            FROM vw_transaction_analytics
            WHERE transaction_type = 'BUY' AND transaction_date_only >= ?
        """.trimIndent())

        val params = mutableListOf<Any>(
            Date.valueOf(fromDate)
        )

        if (platform != null) {
            sql.append(" AND transaction_platform = ?")
            params.add(platform.name)
        }

        sql.append("""
            GROUP BY year, month
            ORDER BY year, month
        """)

        return jdbcTemplate.query(sql.toString(), monthlyInvestedRowMapper, *params.toTypedArray())
    }

    /**
     * Returns the date of the first BUY transaction (transaction_date_only) or null if none.
     */
    fun getFirstInvestmentDate(platform: Platform? = null): LocalDate? {
        val sql = StringBuilder("""
            SELECT MIN(transaction_date_only) 
            FROM vw_transaction_analytics 
            WHERE transaction_type = 'BUY'
        """.trimIndent())
        val params = mutableListOf<Any>()

        if (platform != null) {
            sql.append(" AND transaction_platform = ?")
            params.add(platform.name)
        }
        val date = jdbcTemplate.queryForObject(sql.toString(), Date::class.java, *params.toTypedArray())
        return date?.toLocalDate()
    }

    /**
     * Returns monthly invested rows for the given number of months ending this month.
     * If months is null, the range starts from the first BUY transaction date.
     * This method fills missing months with zero invested.
     */
    fun findMonthlyInvested(months: Int?, platform: Platform? = null): List<MonthlyInvestedRow> {
        if (months != null && months <= 0) return emptyList()

        val fromDate: LocalDate = if (months == null) {
            // start from the first investment date
            getFirstInvestmentDate(platform) ?: return emptyList()
        } else {
            val startYM = YearMonth.now().minusMonths((months - 1).toLong())
            startYM.atDay(1)
        }

        val raw = findMonthlyInvestedFrom(fromDate, platform)

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
    fun findMonthlyDividendsFrom(fromDate: LocalDate, platform: Platform? = null): List<MonthlyDividendRow> {
        val sql = StringBuilder("""
            SELECT 
                year,
                month,
                SUM(transaction_value_eur) AS amount
            FROM vw_transaction_analytics
            WHERE transaction_type = 'DIVIDEND' AND transaction_date_only >= ?
        """.trimIndent())

        val params = mutableListOf<Any>(
            Date.valueOf(fromDate)
        )

        if (platform != null) {
            sql.append(" AND transaction_platform = ?")
            params.add(platform.name)
        }

        sql.append("""
            GROUP BY year, month
            ORDER BY year, month
        """)

        return jdbcTemplate.query(sql.toString(), monthlyDividendRowMapper, *params.toTypedArray())
    }

    /**
     * Returns the date of the first DIVIDEND transaction or null if none.
     */
    fun getFirstDividendDate(platform: Platform? = null): LocalDate? {
        val sql = StringBuilder("""
            SELECT 
                MIN(transaction_date_only)
            FROM vw_transaction_analytics
            WHERE transaction_type = 'DIVIDEND'
        """.trimIndent())

        val params = mutableListOf<Any>()

        if (platform != null) {
            sql.append(" AND transaction_platform = ?")
            params.add(platform.name)
        }

        val date = jdbcTemplate.queryForObject(sql.toString(), Date::class.java, *params.toTypedArray())
        return date?.toLocalDate()
    }

    /**
     * Returns monthly dividend rows for the given number of months ending this month.
     * If months is null, the range starts from the first DIVIDEND transaction date.
     * This method fills missing months with zero amount.
     */
    fun findMonthlyDividends(months: Int?, platform: Platform? = null): List<MonthlyDividendRow> {
        if (months != null && months <= 0) return emptyList()

        val fromDate: LocalDate = if (months == null) {
            getFirstDividendDate(platform) ?: return emptyList()
        } else {
            val startYM = YearMonth.now().minusMonths((months - 1).toLong())
            startYM.atDay(1)
        }

        val raw = findMonthlyDividendsFrom(fromDate, platform)

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
    fun getLatestAssetSnapshots(platform: Platform? = null): List<AssetSnapshot> {
        val sql = """
            select
                snapshot_date,
                asset_id,
                quantity,
                avg_cost_per_share_eur,
                cost_basis_eur,
                market_price_eur,
                market_value_eur,
                unrealized_pl_eur,
                ticker,
                name,
                asset_type
            from public.get_latest_portfolio_holdings(?)
        """.trimIndent()

        return jdbcTemplate.query(sql, assetSnapshotMapper, platform?.name)
    }

    /**
     * Returns latest asset snapshots grouped by company (one row per company/ETF for the latest snapshot_date).
     * For ETFs, groups by ETF id and name; for stocks, groups by company id and name.
     * Aggregates cost basis, market value and unrealized P/L.
     * Also aggregates tickers associated with each holding.
     */
    fun getLatestAssetSnapshotsGroupedByCompany(platform: Platform? = null): List<AssetSnapshotGroupedByCompany> {
        val sql = """
            select
                snapshot_date,
                holding_id,
                holding_name,
                cost_basis_eur,
                market_value_eur,
                unrealized_pl_eur,
                tickers
            from public.get_latest_portfolio_holdings_grouped(?)
        """.trimIndent()

        return jdbcTemplate.query(sql, assetSnapshotGroupedByCompanyMapper, platform?.name)
    }

    /**
     * Returns latest asset performances (percentage change) computed from the latest snapshot rows.
     * Percentage is computed as:
     *  - if avg_cost_per_share_eur available and > 0 and market_price_eur available: (market_price - avg_cost)/avg_cost*100
     *  - else if unrealized_pl_eur and cost_basis_eur available and cost_basis_eur > 0: unrealized_pl / cost_basis * 100
     *  - else 0
     */
    fun getLatestAssetPerformances(platform: Platform? = null): List<LatestAssetPerformance> {
        val sql = """
            select 
                asset_id,
                ticker,
                name,
                type,
                currency_code,
                percentage
            from public.get_latest_asset_performance_percentage(?)
        """.trimIndent()

        return jdbcTemplate.query(sql, latestAssetPerformanceMapper, platform?.name)
    }

    /**
     * Returns dividend cost yield for each asset.
     * Dividend cost yield is calculated as: (Annualized Dividend / Cost Basis) * 100
     * Annualized dividend is calculated from all dividends since the first purchase of each asset,
     * then annualized based on the holding period.
     */
    fun getAssetDividendCostYield(platform: Platform? = null): List<AssetDividendCostYield> {
        val sql = """
            select
                asset_id,
                ticker,
                name,
                total_dividend_eur,
                cost_basis_eur,
                first_buy_date,
                days_held,
                annualized_dividend_eur,
                dividend_cost_yield
            from public.get_latest_asset_dividend_performance(?)
        """.trimIndent()

        return jdbcTemplate.query(sql, assetDividendCostYieldMapper, platform?.name)
    }

    fun getAssetDividendCostYieldGroupedByCompany(platform: Platform? = null): List<CompanyEtfDividendCostYield> {
        val sql = """
            select
                holding_id,
                holding_name,
                total_dividend_eur,
                total_cost_basis_eur ,
                total_annualized_dividend,
                dividend_cost_yield,
                days_held
            from public.get_latest_holding_dividend_performance(?)
        """.trimIndent()

        return jdbcTemplate.query(sql, assetDividendCostYieldGroupedByCompanyMapper, platform?.name)
    }

    /**
     * Returns total dividend cost yield for the entire portfolio.
     * Dividend cost yield is calculated as: (Annualized Total Dividend / Total Cost Basis) * 100
     * Annualized dividend is calculated from all dividends since the first investment,
     * then annualized based on the overall holding period.
     */
    fun getTotalDividendCostYield(platform: Platform? = null): TotalDividendCostYield? {
        val sql = """
           select 
                total_dividend_eur,
                total_cost_basis_eur,
                first_investment_date,
                days_held,
                annualized_dividend_eur,
                dividend_cost_yield
            from public.get_latest_portfolio_dividend_performance(?)
        """.trimIndent()

        return jdbcTemplate.query(sql, totalDividendCostYieldMapper, platform?.name).firstOrNull()
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

    private val assetSnapshotGroupedByCompanyMapper = RowMapper<AssetSnapshotGroupedByCompany> { rs, _ ->
        AssetSnapshotGroupedByCompany(
            snapshotDate = rs.getDate("snapshot_date").toLocalDate(),
            holdingName = rs.getString("holding_name"),
            costBasisEur = rs.getBigDecimal("cost_basis_eur"),
            marketValueEur = rs.getBigDecimal("market_value_eur"),
            unrealizedPlEur = rs.getBigDecimal("unrealized_pl_eur"),
            tickers = rs.getArray("tickers").array.let { sqlArray ->
                (sqlArray as? Array<*>)?.map { it.toString() } ?: emptyList()
            }
        )
    }

    private val latestAssetPerformanceMapper = RowMapper<LatestAssetPerformance> { rs, _ ->
        LatestAssetPerformance(
            assetId = rs.getObject("asset_id", UUID::class.java),
            ticker = rs.getString("ticker"),
            name = rs.getString("name"),
            type = rs.getString("type"),
            percentage = rs.getBigDecimal("percentage") ?: BigDecimal.ZERO,
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

    private val assetDividendCostYieldGroupedByCompanyMapper = RowMapper<CompanyEtfDividendCostYield> { rs, _ ->
        CompanyEtfDividendCostYield(
            id = rs.getObject("holding_id", UUID::class.java),
            name = rs.getString("holding_name"),
            totalDividendEur = rs.getBigDecimal("total_dividend_eur") ?: BigDecimal.ZERO,
            annualizedDividendEur = rs.getBigDecimal("total_annualized_dividend") ?: BigDecimal.ZERO,
            costBasisEur = rs.getBigDecimal("total_cost_basis_eur") ?: BigDecimal.ZERO,
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

    data class AssetSnapshotGroupedByCompany(
        val snapshotDate: LocalDate,
        val holdingName: String,
        val costBasisEur: BigDecimal?,
        val marketValueEur: BigDecimal?,
        val unrealizedPlEur: BigDecimal?,
        val tickers: List<String>
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
        val percentage: BigDecimal,
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

    data class CompanyEtfDividendCostYield(
        val id: UUID,
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
