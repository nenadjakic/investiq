package com.github.nenadjakic.investiq.data.entity.core

import com.github.nenadjakic.investiq.data.entity.asset.Index
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.math.BigDecimal
import java.util.UUID

/**
 * Represents the country allocation breakdown of a market index.
 *
 * This entity tracks what percentage of an index's holdings are allocated to
 * each country. Multiple ETFs tracking the same index share this allocation data.
 * Each index-country combination is unique.
 */
@Entity
@Table(
    name = "index_country_allocations",
    uniqueConstraints = [UniqueConstraint(columnNames = ["index_id", "country_code"])]
)
data class IndexCountryAllocation(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_id", nullable = false)
    var index: Index,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", nullable = false)
    var country: Country,

    @Column(name = "weight_percentage", nullable = false, columnDefinition = "numeric(5,2)")
    @param:Min(0)
    @param:Max(100)
    var weightPercentage: BigDecimal
) {
    @PrePersist
    fun prePersist() {
        if (id == null) {
            id = UUID.randomUUID()
        }
    }
}
