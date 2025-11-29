package com.github.nenadjakic.investiq.data.entity.transaction

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal

/**
 * Represents a transaction of type "FEE" in the investment tracking system.
 *
 * This type captures costs such as brokerage, platform, or spread fees that are either independent or linked
 * to another transaction like opening or closing a position.
 */

@Entity
@DiscriminatorValue("FEE")
class Fee: Transaction() {

    /**
     * Total monetary amount related to this transaction (e.g. fee amount, dividend amount).
     */
    @Column(name = "amount", precision = 20, scale = 8)
    lateinit var amount: BigDecimal

    /**
     * Link to a related transaction (e.g. fee linked to a buy or sell).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_transaction_id")
    var relatedTransaction: Transaction? = null
}