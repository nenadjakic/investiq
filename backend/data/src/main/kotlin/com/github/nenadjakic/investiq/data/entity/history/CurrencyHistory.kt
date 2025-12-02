package com.github.nenadjakic.investiq.data.entity.history

import com.github.nenadjakic.investiq.data.entity.core.Currency
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents historical exchange rates for currencies.
 * Tracks exchange rate changes over time between a currency and a base currency.
 */
@Entity
@Table(name = "currency_histories")
data class CurrencyHistory(
    /**
     * The primary key identifier for the currency history record.
     */
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "from_currency_code", nullable = false)
    var fromCurrency: Currency,

    @ManyToOne
    @JoinColumn(name = "to_currency_code", nullable = false)
    var toCurrency: Currency,

    @Column(name = "valid_date", nullable = false)
    var validDate: LocalDate,

    /**
     * The exchange rate value relative to the base currency.
     * For example, if USD is base and EUR rate is 0.85,
     * then 1 USD = 0.85 EUR.
     */
    @Column(name = "exchange_rate", nullable = false, precision = 20, scale = 6)
    var exchangeRate: BigDecimal,
) {
    @PrePersist
    fun prePersist() {
        if (id == null) {
            id = UUID.randomUUID()
        }
    }
}