package com.github.nenadjakic.investiq.data.entity.asset

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

/**
 * Entity representing an ETF (Exchange-Traded Fund), which is a specific type of Asset.
 * This class uses single-table inheritance strategy with the discriminator value "ETF".
 */

@Entity
@DiscriminatorValue("ETF")
class Etf: ListedAsset() {

    /**
     * The name of the fund manager for this ETF.
     */
    @Column(name = "fund_manager")
    lateinit var fundManager: String
}