package com.github.nenadjakic.investiq.data.entity.asset

import com.github.nenadjakic.investiq.data.entity.core.Country
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.util.UUID

/**
 * Represents the current geographic (country) allocation breakdown of an ETF.
 *
 * This entity tracks what percentage of the ETF's holdings are allocated to
 * each country, enabling geographic exposure analysis and diversification tracking.
 * Each ETF-country combination is unique.
 */
@Entity
@Table(
    name = "etf_country_allocations",
    uniqueConstraints = [UniqueConstraint(columnNames = ["etf_id", "country_code"])]
)
data class EtfCountryAllocation(
    /**
     * The primary key identifier for this allocation record.
     */
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    /**
     * The ETF whose country allocation is being tracked.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etf_id", nullable = false)
    var etf: Etf,

    /**
     * The country to which the allocation percentage applies.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", nullable = false)
    var country: Country,

    /**
     * The percentage of the ETF allocated to this country.
     * Must be between 0 and 100 (inclusive).
     */
    @param:Min(0)
    @param:Max(100)
    @Column(name = "weight_percentage", nullable = false, precision = 5, scale = 2)
    var weightPercentage: BigDecimal
) {
    /**
     * Generates a UUID for the entity before persisting if not already set.
     */
    @PrePersist
    fun prePersist() {
        if (id == null) {
            id = UUID.randomUUID()
        }
    }
}
