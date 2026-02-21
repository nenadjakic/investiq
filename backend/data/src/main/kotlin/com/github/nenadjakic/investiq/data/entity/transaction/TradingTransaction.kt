package com.github.nenadjakic.investiq.data.entity.transaction

import jakarta.persistence.Column
import jakarta.persistence.Entity
import java.math.BigDecimal

@Entity
class TradingTransaction: AssetTransaction() {
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
     * Total value of the purchase transaction.
     */
    val amount: BigDecimal
        get() = quantity * price
}