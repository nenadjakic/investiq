package com.github.nenadjakic.investiq.data.entity.core

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

/**
 * Represents a continent.
 *
 */
@Entity
@Table(name = "continents")
class Continent(

    @Id
    var id: UUID? = null,

    @Column(name = "name", nullable = false, length = 30, unique = true)
    val name: String,

    @OneToMany(mappedBy = "continent")
    val countries: Set<Country> = emptySet()
)