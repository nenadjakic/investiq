package com.github.nenadjakic.investiq.importer.service

import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.repository.AssetRepository
import com.github.nenadjakic.investiq.data.repository.StagingTransactionRepository
import com.github.nenadjakic.investiq.importer.model.StagingTransactionResponse
import com.github.nenadjakic.investiq.importer.model.toStagingTransactionResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StagingTransactionService(
    private val stagingTransactionRepository: StagingTransactionRepository,
    private val assetRepository: AssetRepository) {

    @Transactional
    fun listStagingTransactions(
        unresolved: Boolean,
        platform: Platform,
        limit: Int?
    ): List<StagingTransactionResponse> =
        stagingTransactionRepository.findByPlatformAndResolvedStatus(platform, if (unresolved) ImportStatus.PENDING else null)
            .let { if (limit != null) it.take(limit) else it }
            .map { it.toStagingTransactionResponse() }

    @Transactional()
    fun findById(id: UUID): StagingTransactionResponse =
        stagingTransactionRepository.findById(id)
        .orElseThrow { IllegalArgumentException("Staging transaction not found: $id") }
        .toStagingTransactionResponse()

    @Transactional
    fun assignAsset(id: UUID, assetId: UUID): StagingTransactionResponse {
        val stagingTransaction = stagingTransactionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Staging transaction not found: $id") }

        val stagingTransactions = stagingTransactionRepository.findByrelatedStagingTransaction_Id(id)

        val asset = assetRepository.findById(assetId)
            .orElseThrow { IllegalArgumentException("Asset not found: $id") }

        stagingTransaction.resolvedAsset = asset

        if (!stagingTransactions.isEmpty()) {
            stagingTransactions.forEach { it.resolvedAsset = asset }
        }

        stagingTransactionRepository.saveAll(stagingTransactions)

        return stagingTransactionRepository.save(stagingTransaction).toStagingTransactionResponse()
    }

    @Transactional
    fun updateStatus(id: UUID, importStatus: ImportStatus): StagingTransactionResponse {
        val stagingTransaction = stagingTransactionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Staging transaction not found: $id") }

        val stagingTransactions = stagingTransactionRepository.findByrelatedStagingTransaction_Id(id)

        stagingTransaction.importStatus = importStatus

        if (!stagingTransactions.isEmpty()) {
            stagingTransactions.forEach { it.importStatus = importStatus }
        }
        stagingTransactionRepository.saveAll(stagingTransactions)
        return stagingTransactionRepository.save(stagingTransaction).toStagingTransactionResponse()
    }

    @Transactional
    fun updateStatuses(ids: List<UUID>, importStatus: ImportStatus) {
        stagingTransactionRepository.bulkUpdateStatus(
            ids = ids,
            newStatus = importStatus
        )
    }
}