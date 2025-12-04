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
 * Represents a "DIVIDEND" transaction in the investment tracking system.
 *
 * This transaction type captures the receipt of dividend payments from assets such as stocks or ETFs.
 * It optionally includes tax information and can be linked to a specific position.
 */

@Entity
@DiscriminatorValue("DIVIDEND")
class Dividend: Transaction() {

    /**
     * Asset related to this transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    lateinit var asset: Asset

    /**
     * Total monetary amount related to this transaction.
     */
    @Column(name = "gross_amount", precision = 20, scale = 8)
    lateinit var grossAmount: BigDecimal

    /**
     * Tax percentage applied, with 4 decimal places (e.g., 0.1750 = 17.5%).
     */
    @Column(name = "tax_percentage", precision = 10, scale = 4)
    var taxPercentage: BigDecimal? = null

    /**
     * Tax amount applied on the transaction.
     */
    @Column(name = "tax_amount", precision = 20, scale = 8)
    var taxAmount: BigDecimal? = null


    /**
     * Net amount of dividend received.
     */
    @Column(name = "amount", precision = 20, scale = 8)
    lateinit var amount: BigDecimal

    /**
     * Link to a related transaction (e.g. dividend linked to a buy or sell).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_transaction_id")
    var relatedTransaction: Transaction? = null
}