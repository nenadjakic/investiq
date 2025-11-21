package com.github.nenadjakic.investiq.data.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

/**
 * Represents a company registered in the system. *
 */
@Entity
@Table(name = "companies")
data class Company(

    /**
     * The primary key identifier for the company.
     */
    @Id
    @Column(name = "company_id", nullable = false)
    val companyId: UUID? = null,

    /**
     * Company name (e.g., "Acme Corp.", "Example Technologies, Inc.").
     */
    @Column(name = "name", length = 100, nullable = false)
    val name: String,

    /**
     * The country in which the company is registered or associated.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "country_code", nullable = false)
    val country: Country,

    /**
     * The industry associated with this stock.
     *
     * This is a mandatory many-to-one relationship; the `industry_id` foreign key column
     * is non-nullable in the database.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "industry_id")
    val industry: Industry

    /**
     * A collection of assets owned or linked to this company.
     */
    //@OneToMany(mappedBy = "company")
    //val assets: MutableSet<Asset> = mutableSetOf()
)
