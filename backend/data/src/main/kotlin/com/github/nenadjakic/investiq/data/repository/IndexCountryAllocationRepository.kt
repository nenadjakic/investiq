package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.core.IndexCountryAllocation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * Repository interface for IndexCountryAllocation entities.
 * Provides methods to query country allocation data for market indices.
 */
interface IndexCountryAllocationRepository : JpaRepository<IndexCountryAllocation, UUID> {
    
    /**
     * Finds all country allocations for a given index.
     *
     * @param indexId The UUID of the index
     * @return List of all country allocations for the index
     */
    fun findByIndex_Id(indexId: UUID): List<IndexCountryAllocation>
}
