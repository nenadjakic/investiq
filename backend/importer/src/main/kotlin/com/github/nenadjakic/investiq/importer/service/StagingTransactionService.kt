package com.github.nenadjakic.investiq.importer.service

import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.repository.AssetRepository
import com.github.nenadjakic.investiq.data.repository.StagingTransactionRepository
import com.github.nenadjakic.investiq.importer.model.StagingTransactionResponse
import com.github.nenadjakic.investiq.importer.model.toAssetResponse
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
        stagingTransactionRepository.findByPlatformAndResolvedStatus(platform.displayName, if (unresolved) ImportStatus.PENDING else null)
            .let { if (limit != null) it.take(limit) else it }
            .map { it.toStagingTransactionResponse() }

    fun findById(id: UUID): StagingTransactionResponse =
        stagingTransactionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Staging transaction not found: $id") }
            .let { it.toStagingTransactionResponse() }

    fun assignAsset(id: UUID, assetId: UUID): StagingTransactionResponse {
        var stagingTransaction = stagingTransactionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Staging transaction not found: $id") }

        var asset = assetRepository.findById(assetId)
            .orElseThrow { IllegalArgumentException("Asset not found: $id") }

        stagingTransaction.resolvedAsset = asset

        return stagingTransactionRepository.save(stagingTransaction)
            .let { it.toStagingTransactionResponse() }
    }

    fun updateStatus(id: UUID, importStatus: ImportStatus): StagingTransactionResponse {
        var stagingTransaction = stagingTransactionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Staging transaction not found: $id") }

        stagingTransaction.importStatus = importStatus

        return stagingTransactionRepository.save(stagingTransaction)
            .let { it.toStagingTransactionResponse() }
    }
}