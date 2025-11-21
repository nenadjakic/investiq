package com.github.nenadjakic.investiq.data.entity.core

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

/**
 * Represents a label or categorization that can be attached to one or more transactions.
 */
@Entity
@Table(name = "tags")
data class Tag (
    /**
     * The primary key for the tag.
     */
    @Id
    var id: UUID? = null,

    /**
     * Human-readable name of the tag. Must be unique.
     */
    @Column(name = "name", nullable = false, unique = true, length = 50)
    val name: String,
)