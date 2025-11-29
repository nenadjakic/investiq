package com.github.nenadjakic.investiq.commonservice.service.model

import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.TransactionType
import java.time.OffsetDateTime
import java.util.UUID

data class StagingTransactionResponse (
    val id: UUID,
    val date: OffsetDateTime,
    val type: TransactionType,
    val symbol: String?,
    val quantity: Double?,
    val price: Double?,
    val amount: Double?,
    val resolvedAsset: AssetResponse?,
    val importStatus: ImportStatus
)

fun StagingTransaction.toStagingTransactionResponse() =
    StagingTransactionResponse(
        id = this.id!!,
        date = this.transactionDate,
        type = this.transactionType,
        symbol = this.externalSymbol,
        quantity = this.quantity,
        price = this.price,
        amount = this.amount,
        resolvedAsset = this.resolvedAsset?.toAssetResponse(),
        importStatus = this.importStatus
    )

data class StagingTransactionSimpleResponse (
    val id: UUID,
    val date: OffsetDateTime,
    val type: TransactionType,
    val symbol: String?,
    val resolvedAsset: AssetResponse?,
    val importStatus: ImportStatus
)