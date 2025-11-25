package com.github.nenadjakic.investiq.data.entity.history

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Represents historical price data for financial assets.
 * Tracks price changes over time with support for OHLC (Open, High, Low, Close) data.
 */
@Entity
@Table(name = "asset_histories")
data class AssetHistory(
    /**
     * The primary key identifier for the asset history record.
     */
    @Id
    @Column(name = "id", nullable = false)
    val id: UUID? = null,

    /**
     * The asset for which price history is being tracked.
     */
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    val asset: Asset,

    /**
     * The start date and time when this price became valid.
     */
    @Column(name = "valid_from", nullable = false)
    val validFrom: OffsetDateTime,

    /**
     * The end date and time when this price stopped being valid.
     * Null indicates this is the current price.
     */
    @Column(name = "valid_to")
    val validTo: OffsetDateTime? = null,

    /**
     * Trading volume during this period.
     * Optional field representing number of shares/units traded.
     */
    @Column(name = "volume")
    val volume: Long? = null,

    /**
     * Opening price for the trading period.
     * Optional - used for daily/intraday OHLC data.
     */
    @Column(name = "open_price", precision = 20, scale = 6)
    val openPrice: BigDecimal? = null,

    /**
     * Highest price during the trading period.
     * Optional - used for daily/intraday OHLC data.
     */
    @Column(name = "high_price", precision = 20, scale = 6)
    val highPrice: BigDecimal? = null,

    /**
     * Lowest price during the trading period.
     * Optional - used for daily/intraday OHLC data.
     */
    @Column(name = "low_price", precision = 20, scale = 6)
    val lowPrice: BigDecimal? = null,

    /**
     * Closing price for the trading period.
     */
    @Column(name = "close_price", precision = 20, scale = 6)
    val closePrice: BigDecimal,

    /**
     * Adjusted closing price accounting for corporate actions (splits, dividends).
     * Optional - used for accurate historical analysis.
     */
    @Column(name = "adjusted_close", precision = 20, scale = 6)
    val adjustedClose: BigDecimal? = null,

)