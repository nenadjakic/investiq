package com.github.nenadjakic.investiq.data.entity.core

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/**
 * Represents a country where exchanges operate.
 *
 */
@Entity
@Table(name = "countries")
data class Country(

    /**
     * The primary key identifier for the country.
     * ISO 3166-1 alpha-2 code (e.g. "DE").
     */
    @Id
    @Column(name = "iso2_code", nullable = false, length = 2, unique = true)
    var iso2Code: String? = null,

    /**
     * Full name of the country (e.g., "Germany").
     */
    @Column(name = "name", nullable = false, length = 200, unique = true)
    val name: String,

    @ManyToOne
    @JoinColumn(name = "continent_id")
    val continent: Continent
)