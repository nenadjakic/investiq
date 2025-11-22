package com.github.nenadjakic.investiq.data.entity.transaction

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.math.BigDecimal

/**
 * Represents a transaction of type "DEPOSIT" in the investment tracking system.
 *
 * This type captures funds added to the investment account, increasing the available balance
 * for future investments.
 */
@Entity
@DiscriminatorValue("DEPOSIT")
class Deposit: Transaction() {

    /**
     * Total monetary amount related to this deposit transaction.
     */
    @Column(name = "amount", precision = 20, scale = 6)
    lateinit var amount: BigDecimal
}