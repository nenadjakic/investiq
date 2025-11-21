package com.github.nenadjakic.investiq.data.entity.core

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID


@Entity
@Table(
    name = "exchanges",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["symbol", "country_code"]),
        UniqueConstraint(columnNames = ["name", "country_code"])
    ]
)
data class Exchange(

    /**
     * The primary key identifier for the exchange.
     */
    @Id
    @Column(name = "exchange_id", nullable = false)
    var id: UUID?,

    /**
     * ISO 10383 Market Identifier Code (MIC), e.g., "XFRA".
     */
    @Column(name = "mic", nullable = false, unique = true, length = 10)
    val mic: String,

    /**
     * Short acronym used for the exchange, e.g., "FSE".
     */
    @Column(name = "acronym", nullable = true, length = 10)
    val acronym: String? = null,

    /**
     * Full name of the stock exchange (e.g., "Frankfurt Stock Exchange").
     */
    @Column(name = "name", length = 100, nullable = false)
    val name: String,

    /**
     * Country where the exchange is located.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "country_iso2_code", nullable = false)
    val country: Country,

    //@OneToMany(mappedBy = "exchange")
    //val assets: MutableSet<Asset> = mutableSetOf(),
) {
    @PrePersist
    fun prePersist() {
        if (id == null) {
            id = UUID.randomUUID()
        }
    }
}