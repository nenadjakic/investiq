package com.github.nenadjakic.investiq.data.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
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

    fun findDailyValuesBetween(startDate: LocalDate, endDate: LocalDate): List<PortfolioDailyValue> {
        val sql = """
            SELECT snapshot_date,
                SUM(cost_basis_eur) as total_invested, 
                SUM(market_value_eur) as total_value
            FROM asset_daily_snapshots
            WHERE snapshot_date BETWEEN ? AND ?
            GROUP BY snapshot_date
            order by snapshot_date
        """.trimIndent()
        return jdbcTemplate.query(sql, portfolioDailyValueMapper, startDate, endDate)
    }

    // New method: aggregate current/latest holdings value by industry and sector
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

    // New: aggregate by company country
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

    // New: aggregate by asset currency
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

    fun findMonthlyInvestedFrom(fromDate: LocalDate): List<MonthlyInvestedRow> {
        val sql = """
            SELECT year, month, SUM(transaction_value_eur + COALESCE(fee_amount_eur,0)) AS invested
            FROM vw_transaction_analytics
            WHERE transaction_type = 'BUY' AND transaction_date_only >= ?
            GROUP BY year, month
            ORDER BY year, month
        """.trimIndent()

        return jdbcTemplate.query(sql, monthlyInvestedRowMapper, Date.valueOf(fromDate),)
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

    // New mappers and data classes for country and currency
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
        val platform: String,
        val quantity: BigDecimal,
        val avgCostPerShareEur: BigDecimal?,
        val costBasisEur: BigDecimal?,
        val marketPriceEur: BigDecimal?,
        val marketValueEur: BigDecimal?,
        val unrealizedPlEur: BigDecimal?,
        val ticker: String,
        val name: String
    )

    data class MonthlyInvestedRow(
        val year: Int,
        val month: Int,
        val invested: BigDecimal
    )

    // New result types
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
}
