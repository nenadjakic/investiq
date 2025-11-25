package com.github.nenadjakic.investiq.data.entity.history

import com.github.nenadjakic.investiq.data.entity.core.Currency
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
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
    val id: UUID? = null,

    /**
     * The currency for which the exchange rate is being tracked.
     */
    @ManyToOne
    @JoinColumn(name = "currency_code", nullable = false)
    val currency: Currency,

    /**
     * The start date and time when this exchange rate became valid.
     */
    @Column(name = "valid_from", nullable = false)
    val validFrom: LocalDateTime,

    /**
     * The end date and time when this exchange rate stopped being valid.
     * Null indicates the rate is currently active.
     */
    @Column(name = "valid_to")
    val validTo: LocalDateTime? = null,

    /**
     * The exchange rate value relative to the base currency.
     * For example, if USD is base and EUR rate is 0.85,
     * then 1 USD = 0.85 EUR.
     */
    @Column(name = "exchange_rate", nullable = false, precision = 20, scale = 6)
    val exchangeRate: BigDecimal,

    /**
     * The base currency against which the exchange rate is calculated.
     * Typically a major currency like USD or EUR.
     */
    @ManyToOne
    @JoinColumn(name = "base_currency_code", nullable = false)
    val baseCurrency: Currency,

    /**
     * The source of the exchange rate data.
     * Optional field (e.g., "ECB", "Yahoo Finance", "Manual Entry").
     */
    @Column(name = "source", length = 100)
    val source: String? = null,

    /**
     * Timestamp when this record was created in the database.
     */
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)