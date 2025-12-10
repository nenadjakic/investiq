package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.transaction.Dividend
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.queryForList
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