package com.github.nenadjakic.investiq.data.entity.transaction

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

/**
 * Represents an "sell" transaction in the investment system.
 * This transaction type is used when a asset position is being sold (e.g., buying stocks, ETFs, etc.).
 *
 * In this transaction, a certain quantity of the asset is sell at a specific price per unit.
 * It serves as the entry point for tracking asset positions, which may later be partially or fully closed.
 */

@Entity
@DiscriminatorValue("SELL")
class Sell: Transaction() {

    /**
     * Asset related to this transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    lateinit var asset: Asset

    /**
     * Quantity involved in this transaction.
     */
    @Column(precision = 20, scale = 12)
    lateinit var quantity: BigDecimal

    /**
     * Price per unit for transactions where it makes sense (buy/sell).
     */
    @Column(precision = 20, scale = 8)
    lateinit var price: BigDecimal

    /**
     * Total value of the sell transaction.
     */
    val amount: BigDecimal
        get() = quantity * price
}