package com.github.nenadjakic.investiq.importer.model

import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.enum.TransactionType
import java.util.UUID

data class StagingTransactionResponse (
    val id: UUID,
    val date: String,
    val type: TransactionType,
    val symbol: String?,
    val quantity: Double?,
    val price: Double?,
    val amount: Double?,
    val resolvedAsset: AssetResponse?,
    val importStatus: ImportStatus
)