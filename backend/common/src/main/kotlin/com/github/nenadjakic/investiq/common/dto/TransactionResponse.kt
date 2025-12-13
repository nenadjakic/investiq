package com.github.nenadjakic.investiq.common.dto

import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.enum.TransactionType
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class TransactionResponse(
    val id: UUID,
    val transactionType: TransactionType,
    val platform: Platform,
    val date: OffsetDateTime,
    val currency: CurrencyResponse,

    // asset / trade specific
    val asset: AssetResponse? = null,
    val quantity: BigDecimal? = null,
    val price: BigDecimal? = null,

    // amount / deposit / withdrawal / fee
    val amount: BigDecimal? = null,
    val relatedTransactionId: UUID? = null,

    // dividend specific
    val grossAmount: BigDecimal? = null,
    val taxAmount: BigDecimal? = null,
    val taxPercentage: BigDecimal? = null
)