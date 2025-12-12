package com.github.nenadjakic.investiq.data.entity.transaction

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.math.BigDecimal

@Entity
@DiscriminatorValue("DIVIDEND_ADJUSTMENT")
class DividendAdjustment: Transaction() {

    @Column(name = "amount", precision = 20, scale = 8)
    lateinit var amount: BigDecimal
}