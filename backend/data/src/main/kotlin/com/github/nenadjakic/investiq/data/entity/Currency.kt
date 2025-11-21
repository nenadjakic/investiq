package com.github.nenadjakic.investiq.data.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal

/**
 * Represents a currency used for trading assets.
 *
 */
@Entity
@Table(name = "currencies")
data class Currency(
    /**
     * The primary key identifier for the currency.
     * The ISO 4217 currency code.
     */
    @Id
    @Column(name = "code", nullable = false, length = 3)
    var code: String? = null,

    /**
     * The full name of the currency.
     * Cannot be null.
     */
    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    /**
     * The symbol representing the currency.
     * Optional field (e.g., $, €, ¥).
     */
    @Column(name = "symbol", length = 10)
    val symbol: String? = null,

    @ManyToOne
    @JoinColumn(name = "parent_code")
    val parent: Currency? = null,

    @Column(name = "to_parent_factor", precision = 20, scale = 6)
    val toParentFactor: BigDecimal = BigDecimal.ONE,

    @OneToMany(mappedBy = "parent")
    val children: MutableSet<Currency> = mutableSetOf()
)