package com.github.nenadjakic.investiq.data.entity.core

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID


/**
 * Represents a high-level economic or market segment that groups related industries.
 *
 * Examples include:
 * - "Technology"
 * - "Healthcare"
 * - "Energy"
 *
 * Sectors are used to classify assets for analytical, reporting, and diversification purposes.
 */

@Entity
@Table(name = "sectors")
class Sector(

    /**
     * The primary key identifier for the sector.
     * Automatically generated UUID assigned before the entity is saved.
     */
    @Id
    var id: UUID? = null,

    /**
     * Name of the sector (e.g., "Technology").
     */
    @Column(name = "name", nullable = false, unique = true, length = 100)
    var name: String,

    /**
     * List of industries that belong to this sector.
     */
    @OneToMany(mappedBy = "sector", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var industries: MutableList<Industry> = mutableListOf(),
)