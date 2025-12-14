package com.github.nenadjakic.investiq.service


import com.github.nenadjakic.investiq.common.dto.StagingTransactionEditRequest
import com.github.nenadjakic.investiq.common.dto.StagingTransactionResponse
import com.github.nenadjakic.investiq.common.dto.toStagingTransactionResponse
import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.repository.AssetRepository
import com.github.nenadjakic.investiq.data.repository.StagingTransactionRepository
import jakarta.transaction.Transactional
import org.hibernate.Hibernate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StagingTransactionService(
    private val stagingTransactionRepository: StagingTransactionRepository,
    private val assetRepository: AssetRepository
) {

    @Deprecated(message = "Use findAll")
    @Transactional
    fun listStagingTransactions(
        unresolved: Boolean,
        platform: Platform?,
        limit: Int?
    ): List<StagingTransactionResponse> =
        stagingTransactionRepository.findByPlatformAndImportStatus(platform, if (unresolved) ImportStatus.PENDING else null)
            .let { if (limit != null) it.take(limit) else it }
            .map { it.toStagingTransactionResponse() }

    @Transactional
    fun findAll(
        platform: Platform?,
        importStatus: ImportStatus?,
        pageable: Pageable
    ): Page<StagingTransactionResponse> {
        val specification = Specification<StagingTransaction> { root, query, cb ->
            var predicate = cb.conjunction()

            if (platform != null) {
             predicate = cb.and(
                 predicate,
                 cb.equal(root.get<Platform>("platform"), platform)
             )
            }

            if (importStatus != null) {
                predicate = cb.and(
                    predicate,
                    cb.equal(root.get<ImportStatus>("importStatus"), importStatus)
                )
            }

            predicate
        }
        return stagingTransactionRepository.findAll(specification, pageable)
            .map { Hibernate.unproxy(it, StagingTransaction::class.java) }
            .map { unproxied ->
                val stagingTransaction = unproxied as StagingTransaction

                stagingTransaction.resolvedAsset = stagingTransaction.resolvedAsset?.let { Hibernate.unproxy(it, Asset::class.java) as Asset }

                val unproxiedRelated = stagingTransaction.relatedStagingTransactions
                    .map { Hibernate.unproxy(it, StagingTransaction::class.java) as StagingTransaction }
                    .toMutableSet()
                stagingTransaction.relatedStagingTransactions.clear()
                stagingTransaction.relatedStagingTransactions.addAll(unproxiedRelated)

                stagingTransaction
            }
            .map { it.toStagingTransactionResponse() }
    }

    @Transactional()
    fun findById(id: UUID): StagingTransactionResponse =
        stagingTransactionRepository.findById(id)
        .orElseThrow { IllegalArgumentException("Staging transaction not found: $id") }
        .also {  Hibernate.unproxy(it, StagingTransaction::class.java) }
        .toStagingTransactionResponse()

    @Transactional
    fun update(request: StagingTransactionEditRequest) {
        val stagingTransaction = stagingTransactionRepository
            .findById(request.id!!)
            .orElseThrow { IllegalArgumentException() }

        request.asset?.let { stagingTransaction.resolvedAsset = assetRepository.getReferenceById(request.asset!! )}
        request.quantity?.let { stagingTransaction.quantity = request.quantity }
        request.price?.let {  stagingTransaction.price = request.price }
        request.amount?.let {  stagingTransaction.amount = request.amount }
        request.grossAmount?.let {  stagingTransaction.grossAmount = request.grossAmount }
        request.taxPercentage?.let {  stagingTransaction.taxPercentage = request.taxPercentage }
        request.taxAmount?.let {  stagingTransaction.taxAmount = request.taxAmount }

        stagingTransactionRepository.save(stagingTransaction)
    }

    @Transactional
    fun updateStatus(ids: List<UUID>, importStatus: ImportStatus): Int {
        val updatedCount = stagingTransactionRepository.bulkUpdateStatus(
            ids = ids,
            newStatus = importStatus,
            pendingStatus = ImportStatus.PENDING
        )

        if (updatedCount == 0) {
            throw IllegalArgumentException("No staging transactions were updated for ids: $ids")
        }

        return updatedCount
    }

    @Deprecated("Use update")
    @Transactional
    fun assignAsset(id: UUID, assetId: UUID): StagingTransactionResponse {
        val stagingTransaction = stagingTransactionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Staging transaction not found: $id") }
            .also { Hibernate.unproxy(it, StagingTransaction::class.java) }

        val stagingTransactions = stagingTransactionRepository.findByRelatedStagingTransaction_Id(id)

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
            .also { Hibernate.unproxy(it, StagingTransaction::class.java) }

        val stagingTransactions = stagingTransactionRepository.findByRelatedStagingTransaction_Id(id)

        stagingTransaction.importStatus = importStatus

        if (!stagingTransactions.isEmpty()) {
            stagingTransactions.forEach { it.importStatus = importStatus }
        }
        stagingTransactionRepository.saveAll(stagingTransactions)
        return stagingTransactionRepository.save(stagingTransaction).toStagingTransactionResponse()
    }
}