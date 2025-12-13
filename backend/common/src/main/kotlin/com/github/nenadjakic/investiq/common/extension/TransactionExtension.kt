package com.github.nenadjakic.investiq.common.extension

import com.github.nenadjakic.investiq.common.dto.TransactionResponse
import com.github.nenadjakic.investiq.common.dto.toAssetResponse
import com.github.nenadjakic.investiq.common.dto.toResponse
import com.github.nenadjakic.investiq.data.entity.transaction.*

fun Transaction.toTransactionResponse(): TransactionResponse {
    val relatedId = when (this) {
        is Fee -> this.relatedTransaction?.id
        is Dividend -> this.relatedTransaction?.id
        else -> null
    }

    return TransactionResponse(
        id = this.id!!,
        transactionType = this.transactionType,
        platform = this.platform,
        date = this.date,
        currency = this.currency.toResponse(),
        asset = when (this) {
            is Buy -> this.asset.toAssetResponse()
            is Sell -> this.asset.toAssetResponse()
            is Dividend -> this.asset.toAssetResponse()
            else -> null
        },
        quantity = when (this) {
            is Buy -> this.quantity
            is Sell -> this.quantity
            else -> null
        },
        price = when (this) {
            is Buy -> this.price
            is Sell -> this.price
            else -> null
        },
        amount = when (this) {
            is Fee -> this.amount
            is Deposit -> this.amount
            is Dividend -> this.amount
            is DividendAdjustment -> this.amount
            is Buy -> this.amount
            is Sell -> this.amount
            else -> null
        },
        relatedTransactionId = relatedId,
        grossAmount = when (this) {
            is Dividend -> this.grossAmount
            else -> null
        },
        taxAmount = when (this) {
            is Dividend -> this.taxAmount
            else -> null
        },
        taxPercentage = when (this) {
            is Dividend -> this.taxPercentage
            else -> null
        }
    )
}
