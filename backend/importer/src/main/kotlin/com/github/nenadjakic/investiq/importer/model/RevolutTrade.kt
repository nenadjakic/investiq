package com.github.nenadjakic.investiq.importer.model

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.AssetAlias
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Tag
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.enum.TransactionType
import com.github.nenadjakic.investiq.importer.enum.RevolutAction
import java.time.LocalDateTime
import java.time.ZoneOffset

data class RevolutTrade (
    val action: RevolutAction,
    val time: LocalDateTime,
    val ticker: String?,
    val quantity: Double?,
    val price: Double?,
    val totalAmount: Double,
    val currency: String,
    var fxRate: Double,
    var commissionAmount: Double? = null
)

fun RevolutTrade.toStagingTransactions(
    assetAliases: Collection<AssetAlias>,
    currencies: Map<String, Currency>,
    tags: MutableMap<String, Tag>,

    ): Collection<StagingTransaction> {
    val stagingTransactions = mutableListOf<StagingTransaction>()

    val asset: Asset?
    val assets = assetAliases.filter {
        it.platform == Platform.REVOLUT && it.externalSymbol.equals(
            this.ticker, ignoreCase = true
        )
    }

    asset = if (!assets.isEmpty()) {
        assets.first().asset
    } else {
        null
    }

    when (this.action) {
        RevolutAction.DIVIDEND -> {
            stagingTransactions.add(
                StagingTransaction(
                    platform = Platform.REVOLUT,
                    transactionType = TransactionType.DIVIDEND,
                    importStatus = ImportStatus.PENDING,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    quantity = this.quantity,
                    price = this.price,
                    externalSymbol = this.ticker,
                    resolvedAsset = asset,
                    grossAmount = this.totalAmount,
                    taxAmount = 0.0,
                    currency = currencies[this.currency],
                ).also {
                    it.amount = it.grossAmount!! - it.taxAmount!!
                }
            )
        }

        RevolutAction.DEPOSIT -> {
            val deposit = StagingTransaction(
                platform = Platform.REVOLUT,
                transactionType = TransactionType.DEPOSIT,
                importStatus = ImportStatus.PENDING,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                currency = currencies[this.currency],
                amount = this.totalAmount,
            )
            stagingTransactions.add(deposit)
        }

        RevolutAction.MARKET_BUY -> {
            val buy = StagingTransaction(
                platform = Platform.REVOLUT,
                transactionType = TransactionType.BUY,
                importStatus = ImportStatus.PENDING,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                price = this.price,
                quantity = this.quantity,
                externalSymbol = this.ticker,
                resolvedAsset = asset,
            )
            stagingTransactions.add(buy)
            if (this.commissionAmount != null) {
                stagingTransactions.add(
                    StagingTransaction(
                        platform = Platform.REVOLUT,
                        transactionType = TransactionType.FEE,
                        importStatus = ImportStatus.PENDING,
                        transactionDate = this.time.atOffset(ZoneOffset.UTC),
                        currency = currencies[this.currency],
                        amount = this.commissionAmount,
                        relatedStagingTransaction = buy
                    )
                )
            }
        }

        RevolutAction.MARKET_SELL -> {
            val sell = StagingTransaction(
                platform = Platform.REVOLUT,
                transactionType = TransactionType.BUY,
                importStatus = ImportStatus.PENDING,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                price = this.price,
                quantity = this.quantity,
                externalSymbol = this.ticker,
                resolvedAsset = asset,
            )
            stagingTransactions.add(sell)
            if (this.commissionAmount != null) {
                stagingTransactions.add(
                    StagingTransaction(
                        platform = Platform.REVOLUT,
                        transactionType = TransactionType.FEE,
                        importStatus = ImportStatus.PENDING,
                        transactionDate = this.time.atOffset(ZoneOffset.UTC),
                        currency = currencies[this.currency],
                        amount = this.commissionAmount,
                        relatedStagingTransaction = sell
                    )
                )
            }
        }

        else -> {
            val unknown = StagingTransaction(
                platform = Platform.REVOLUT,
                transactionType = TransactionType.UNKNOWN,
                quantity = this.quantity,
                price = this.price,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                externalSymbol = this.ticker,
                resolvedAsset = asset,
                importStatus = ImportStatus.PENDING,
            )
            stagingTransactions.add(unknown)
            if (this.commissionAmount != null) {
                stagingTransactions.add(
                    StagingTransaction(
                        platform = Platform.REVOLUT,
                        transactionType = TransactionType.FEE,
                        importStatus = ImportStatus.PENDING,
                        transactionDate = this.time.atOffset(ZoneOffset.UTC),
                        currency = currencies[this.currency],
                        amount = this.commissionAmount,
                        relatedStagingTransaction = unknown
                    )
                )
            }
        }
    }

    return stagingTransactions
}