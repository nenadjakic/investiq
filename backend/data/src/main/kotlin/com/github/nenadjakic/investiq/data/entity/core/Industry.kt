package com.github.nenadjakic.investiq.data.entity.core

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID


/**
 * Represents a more specific classification within a sector, grouping companies with similar business activities.
 *
 * Examples:
 * - Sector: "Technology" → Industry: "Semiconductors"
 * - Sector: "Healthcare" → Industry: "Biotechnology"
 *
 * Industries are nested under sectors and help refine asset classification for analysis and comparison.
 */

@Entity
@Table(name = "industries")
data class Industry (

    /**
     * The primary key identifier for the industry.
     * Automatically generated UUID assigned before the entity is saved.
     */
    @Id
    var id: UUID? = null,

    /**
     * Name of the industry (e.g., "Semiconductors").
     */
    @Column(name = "name", nullable = false, unique = true)
    val name: String,

    /**
     * Sector this industry belongs to.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "sector_id", nullable = false)
    val sector: Sector,
)