package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface StagingTransactionRepository: JpaRepository<StagingTransaction, UUID> {
    @Query("""
        SELECT st FROM StagingTransaction st
        JOIN st.tags tag
        LEFT JOIN st.resolvedAsset ra
        WHERE tag.name = :platformTag
          AND (:importStatus IS NULL OR st.importStatus = :importStatus)
        ORDER BY st.transactionDate ASC
    """)
    fun findByPlatformAndResolvedStatus(
        @Param("platformTag") platformTag: String,
        @Param("importStatus") importStatus: ImportStatus? = null
    ): List<StagingTransaction>
}