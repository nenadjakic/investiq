package com.github.nenadjakic.investiq.data.entity.transaction

import com.github.nenadjakic.investiq.data.entity.core.Currency
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
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
    @Column(name = "amount", precision = 20, scale = 8)
    lateinit var amount: BigDecimal

    @ManyToOne
    @JoinColumn(name = "currency_code")
    lateinit var currency: Currency
}