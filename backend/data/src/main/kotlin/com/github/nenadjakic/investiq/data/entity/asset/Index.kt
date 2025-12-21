package com.github.nenadjakic.investiq.data.entity.asset

import com.github.nenadjakic.investiq.data.entity.core.IndexCountryAllocation
import com.github.nenadjakic.investiq.data.entity.core.IndexSectorAllocation
import jakarta.persistence.CascadeType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany

/**
 * Entity representing a stock market index, which is a specific type of Asset.
 * This class uses single-table inheritance strategy with the discriminator value "INDEX".
 * 
 * Indices track the performance of a collection of stocks or other securities, 
 * such as S&P 500, NASDAQ, STOXX 50, etc.
 */
@Entity
@DiscriminatorValue("INDEX")
class Index : Asset() {
    
    /**
     * Sector allocations for this index.
     * Shared by all ETFs that track this index.
     */
    @OneToMany(mappedBy = "index", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val sectorAllocations: MutableSet<IndexSectorAllocation> = mutableSetOf()

    /**
     * Country allocations for this index.
     * Shared by all ETFs that track this index.
     */
    @OneToMany(mappedBy = "index", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val countryAllocations: MutableSet<IndexCountryAllocation> = mutableSetOf()
}