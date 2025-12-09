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
                SUM(cost_basis_eur) as total_invested
            FROM asset_daily_snapshots
            WHERE snapshot_date = (SELECT MAX(snapshot_date) FROM asset_daily_snapshots)
            GROUP BY snapshot_date
        """.trimIndent()

        return jdbcTemplate.query(sql, portfolioSnapshotMapper).firstOrNull()
    }

    fun getPreviousPortfolioSnapshot(currentDate: LocalDate): PortfolioSnapshot? {
        val sql = """
            SELECT 
                snapshot_date,
                SUM(market_value_eur) as total_value,
                SUM(cost_basis_eur) as total_invested
            FROM asset_daily_snapshots
            WHERE snapshot_date = (
                SELECT MAX(snapshot_date) 
                FROM asset_daily_snapshots 
                WHERE snapshot_date < ?
            )
            GROUP BY snapshot_date
        """.trimIndent()

        return jdbcTemplate.query(sql, portfolioSnapshotMapper, currentDate).firstOrNull()
    }

    private val portfolioSnapshotMapper = RowMapper<PortfolioSnapshot> { rs, _ ->
        PortfolioSnapshot(
            snapshotDate = rs.getDate("snapshot_date").toLocalDate(),
            totalValue = rs.getBigDecimal("total_value"),
            totalInvested = rs.getBigDecimal("total_invested")
        )
    }
}

data class PortfolioSnapshot(
    val snapshotDate: LocalDate,
    val totalValue: BigDecimal,
    val totalInvested: BigDecimal
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