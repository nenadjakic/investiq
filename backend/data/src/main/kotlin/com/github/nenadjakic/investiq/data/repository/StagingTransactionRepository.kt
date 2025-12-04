package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface StagingTransactionRepository: JpaRepository<StagingTransaction, UUID>, JpaSpecificationExecutor<StagingTransaction> {
    @Query("""
        SELECT st FROM StagingTransaction st
        LEFT JOIN FETCH st.resolvedAsset ra
        WHERE (:platform IS NULL OR st.platform = :platform)
          AND (:importStatus IS NULL OR st.importStatus = :importStatus)
          AND st.relatedStagingTransaction IS NULL
        ORDER BY st.transactionDate ASC
    """)
    fun findByPlatformAndImportStatus(
        @Param("platform") platform: Platform? = null,
        @Param("importStatus") importStatus: ImportStatus? = null
    ): List<StagingTransaction>

    fun findByRelatedStagingTransaction_Id(relatedStagingTransaction: UUID): List<StagingTransaction>

    fun findAllByImportStatusAndRelatedStagingTransactionIsNull(importStatus: ImportStatus): List<StagingTransaction>

    @Query("""
        select st.id
        from StagingTransaction st
        where st.importStatus = :status
          and st.relatedStagingTransaction is null
    """)
    fun findIdsForImport(status: ImportStatus): List<UUID>

    @Modifying
    @Transactional
    @Query("""
        update StagingTransaction s
        set s.importStatus = :newStatus
        where (
                s.id in :ids
                or s.relatedStagingTransaction.id in :ids
            )
            and s.importStatus = :pendingStatus
    """)
    fun bulkUpdateStatus(
        @Param("ids") ids: List<UUID>,
        @Param("newStatus") newStatus: ImportStatus,
        @Param("pendingStatus") pendingStatus: ImportStatus = ImportStatus.PENDING
    ): Int
}