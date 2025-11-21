package com.github.nenadjakic.investiq.data.entity

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

/**
 * Entity representing a Stock, which is a specific type of Asset.
 * This class uses single-table inheritance strategy with the discriminator value "STOCK".
 */

@Entity
@DiscriminatorValue("STOCK")
class Stock : ListedAsset() {

    /**
     * The company that issued this stock.
     *
     * This is a mandatory many-to-one relationship; the `company_id` foreign key column
     * is non-nullable in the database.
     */
    @ManyToOne
    @JoinColumn(name = "company_id")
    lateinit var company: Company
}