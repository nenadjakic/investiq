package com.github.nenadjakic.investiq.data.entity.transaction

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.math.BigDecimal

/**
 * Represents a transaction of type "WITHDRAWAL" in the investment tracking system.
 *
 * This type captures funds removed from the investment account, decreasing the available balance
 * for future investments.
 */
@Entity
@DiscriminatorValue("WITHDRAWAL")
class Withdrawal: Transaction() {

    /**
     * Total monetary amount related to this withdraw transaction.
     */
    @Column(name = "amount", precision = 20, scale = 8)
    lateinit var amount: BigDecimal
}