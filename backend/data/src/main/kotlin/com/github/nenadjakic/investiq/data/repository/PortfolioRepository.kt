package com.github.nenadjakic.investiq.data.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

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