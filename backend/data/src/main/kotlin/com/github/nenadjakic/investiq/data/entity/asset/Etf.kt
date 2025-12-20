package com.github.nenadjakic.investiq.data.entity.asset

import com.github.nenadjakic.investiq.data.enum.AssetClass
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.util.UUID

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
    @Column(name = "fund_manager", length = 200, nullable = false)
    lateinit var fundManager: String

    /**
     * The asset class categorization (e.g., EQUITY, BOND, COMMODITY, MIXED).
     * Optional field that helps classify assets for portfolio analysis.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "asset_class", length = 50)
    var assetClass: AssetClass? = null

    /**
     * The index that this ETF tracks (optional).
     * If null, this ETF is actively managed and uses its own sector/country allocations.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracked_index_id", nullable = true)
    var trackedIndex: Index? = null

    /**
     * The sector allocation breakdown for this ETF.
     * Used for ETFs that are actively managed or not tracking a specific index.
     * Lazy-loaded relationship to EtfSectorAllocation entities.
     */
    @OneToMany(mappedBy = "etf", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val sectorAllocations: MutableSet<EtfSectorAllocation> = mutableSetOf()

    /**
     * The country allocation breakdown for this ETF.
     * Used for ETFs that are actively managed or not tracking a specific index.
     * Lazy-loaded relationship to EtfCountryAllocation entities.
     */
    @OneToMany(mappedBy = "etf", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val countryAllocations: MutableSet<EtfCountryAllocation> = mutableSetOf()
}