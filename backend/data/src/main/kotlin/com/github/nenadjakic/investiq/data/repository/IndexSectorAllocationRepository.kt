package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.core.IndexSectorAllocation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * Repository interface for IndexSectorAllocation entities.
 * Provides methods to query sector allocation data for market indices.
 */
interface IndexSectorAllocationRepository : JpaRepository<IndexSectorAllocation, UUID> {
    
    /**
     * Finds all sector allocations for a given index.
     *
     * @param indexId The UUID of the index
     * @return List of all sector allocations for the index
     */
    fun findByIndex_Id(indexId: UUID): List<IndexSectorAllocation>
}
