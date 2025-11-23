package com.github.nenadjakic.investiq.importer.service

import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.repository.StagingTransactionRepository
import com.github.nenadjakic.investiq.importer.model.StagingTransactionResponse
import com.github.nenadjakic.investiq.importer.model.toAssetResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StagingTransactionService(private val stagingTransactionRepository: StagingTransactionRepository) {

    @Transactional
    fun listStagingTransactions(
        unresolved: Boolean,
        platform: Platform,
        limit: Int?
    ): List<StagingTransactionResponse> =
        stagingTransactionRepository.findByPlatformAndResolvedStatus(platform.displayName, if (unresolved) ImportStatus.PENDING else null)
            .let { if (limit != null) it.take(limit) else it }
            .map { stagingTransaction ->
                StagingTransactionResponse(
                    id = stagingTransaction.id!!,
                    date = stagingTransaction.transactionDate,
                    type = stagingTransaction.transactionType,
                    symbol = stagingTransaction.externalSymbol,
                    quantity = stagingTransaction.quantity,
                    price = stagingTransaction.price,
                    amount = stagingTransaction.amount,
                    resolvedAsset = stagingTransaction.resolvedAsset?.toAssetResponse(),
                    importStatus = stagingTransaction.importStatus
                )
            }

    fun findById(id: UUID): StagingTransactionResponse =
        stagingTransactionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Staging transaction not found: $id") }
            .let { stagingTransaction ->
                StagingTransactionResponse(
                    id = stagingTransaction.id!!,
                    date = stagingTransaction.transactionDate,
                    type = stagingTransaction.transactionType,
                    symbol = stagingTransaction.externalSymbol,
                    quantity = stagingTransaction.quantity,
                    price = stagingTransaction.price,
                    amount = stagingTransaction.amount,
                    resolvedAsset = stagingTransaction.resolvedAsset?.toAssetResponse(),
                    importStatus = stagingTransaction.importStatus
                )
            }
}