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
 * Represents the sector allocation breakdown of a market index.
 *
 * This entity tracks what percentage of an index's holdings are allocated to
 * each sector. Multiple ETFs tracking the same index share this allocation data.
 * Each index-sector combination is unique.
 */
@Entity
@Table(
    name = "index_sector_allocations",
    uniqueConstraints = [UniqueConstraint(columnNames = ["index_id", "sector_id"])]
)
data class IndexSectorAllocation(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_id", nullable = false)
    var index: Index,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    var sector: Sector,

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
