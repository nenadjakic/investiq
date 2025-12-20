package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.asset.EtfCountryAllocation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * Repository interface for EtfCountryAllocation entities.
 * Provides methods to query current country allocation data for ETFs.
 */
interface EtfCountryAllocationRepository : JpaRepository<EtfCountryAllocation, UUID> {
    
    /**
     * Finds all country allocations for a given ETF.
     *
     * @param etfId The UUID of the ETF
     * @return List of all country allocations for the ETF
     */
    fun findByEtf_Id(etfId: UUID): List<EtfCountryAllocation>
}
