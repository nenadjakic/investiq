package com.github.nenadjakic.investiq.common.dto.transaction

import com.github.nenadjakic.investiq.data.enum.AssetType
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.enum.TransactionType
import java.time.OffsetDateTime

data class TransactionFilterRequest(
    val platform: Platform?,
    val transactionType: TransactionType?,
    val assetType: AssetType?,
    val assetSymbol: String?,
    val currency: String?,
    val dateFrom: OffsetDateTime?,
    val dateTo: OffsetDateTime?
)
