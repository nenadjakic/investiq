package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.asset.EtfSectorAllocation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * Repository interface for EtfSectorAllocation entities.
 * Provides methods to query current sector allocation data for ETFs.
 */
interface EtfSectorAllocationRepository : JpaRepository<EtfSectorAllocation, UUID> {
    
    /**
     * Finds all sector allocations for a given ETF.
     *
     * @param etfId The UUID of the ETF
     * @return List of all sector allocations for the ETF
     */
    fun findByEtf_Id(etfId: UUID): List<EtfSectorAllocation>
}
