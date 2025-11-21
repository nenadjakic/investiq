package com.github.nenadjakic.investiq.data.entity.transaction

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.math.BigDecimal

/**
 * Represents an "buy" transaction in the investment system.
 * This transaction type is used when a new asset position is being acquired (e.g., buying stocks, ETFs, etc.).
 *
 * In this transaction, a certain quantity of the asset is purchased at a specific price per unit.
 * It serves as the entry point for tracking asset positions, which may later be partially or fully closed.
 */

@Entity
@DiscriminatorValue("BUY")
class Buy: Transaction() {

    /**
     * Quantity involved in this transaction.
     */
    @Column(precision = 20, scale = 6)
    lateinit var quantity: BigDecimal

    /**
     * Price per unit for transactions where it makes sense (buy/sell).
     */
    @Column(precision = 20, scale = 6)
    lateinit var price: BigDecimal
}