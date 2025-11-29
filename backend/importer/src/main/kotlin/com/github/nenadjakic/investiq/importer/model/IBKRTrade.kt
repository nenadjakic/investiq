package com.github.nenadjakic.investiq.importer.model

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.AssetAlias
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Tag
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.enum.TransactionType
import com.github.nenadjakic.investiq.importer.enum.IBKRAction
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs

data class IBKRTrade(
    val action: IBKRAction,
    val time: LocalDateTime,
    val ticker: String? = null,
    val quantity: Double? = null,
    val price: Double? = null,
    val amount: Double? = null,
    var taxAmount: Double? = null,
    var commission: Double? = null,
    var currency: String
)

fun IBKRTrade.toStagingTransactions(
    assetAliases: Collection<AssetAlias>,
    currencies: Map<String, Currency>,
    tags: MutableMap<String, Tag>,

    ): Collection<StagingTransaction> {
    val stagingTransactions = mutableListOf<StagingTransaction>()

    val asset: Asset?
    val assets = assetAliases.filter {
        it.platform == Platform.IBKR && it.externalSymbol.equals(
            this.ticker, ignoreCase = true
        )
    }

    asset = if (!assets.isEmpty()) {
        assets.first().asset
    } else {
        null
    }

    when (this.action) {
        IBKRAction.DIVIDEND -> {
            stagingTransactions.add(
                StagingTransaction(
                    platform = Platform.IBKR,
                    transactionType = TransactionType.DIVIDEND,
                    importStatus = ImportStatus.PENDING,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    quantity = this.quantity,
                    price = this.price,
                    externalSymbol = this.ticker,
                    resolvedAsset = asset,
                    resolutionNote = null,
                    grossAmount = this.amount,
                    taxAmount = abs(this.taxAmount!!),
                    currency = currencies[this.currency],
                ).also {
                    it.amount = it.grossAmount!! - it.taxAmount!!
                    it.taxPercentage = it.taxAmount!! / (it.grossAmount!! - it.taxAmount!!) * 100
                }
            )
        }

        IBKRAction.DEPOSIT -> {
            val deposit = StagingTransaction(
                platform = Platform.IBKR,
                transactionType = TransactionType.DEPOSIT,
                importStatus = ImportStatus.PENDING,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                currency = currencies[this.currency],
                amount = this.amount,
            )
            stagingTransactions.add(deposit)
        }

        IBKRAction.BUY -> {
            val buy = StagingTransaction(
                platform = Platform.IBKR,
                transactionType = TransactionType.BUY,
                importStatus = ImportStatus.PENDING,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                price = this.price,
                quantity = this.quantity,
                externalSymbol = this.ticker,
                resolvedAsset = asset,
                currency = currencies[this.currency],
            )
            stagingTransactions.add(buy)
            if (this.commission != null && this.commission!! != 0.0) {
                stagingTransactions.add(
                    StagingTransaction(
                        platform = Platform.IBKR,
                        transactionType = TransactionType.FEE,
                        importStatus = ImportStatus.PENDING,
                        transactionDate = this.time.atOffset(ZoneOffset.UTC),
                        amount = abs(this.commission!!),
                        currency = currencies[this.currency],
                        relatedStagingTransaction = buy
                    )
                )
            }
        }

        IBKRAction.SELL -> {
            {
                val sell = StagingTransaction(
                    platform = Platform.IBKR,
                    transactionType = TransactionType.SELL,
                    importStatus = ImportStatus.PENDING,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    price = this.price,
                    quantity = abs(this.quantity!!),
                    externalSymbol = this.ticker,
                    resolvedAsset = asset,
                )
                stagingTransactions.add(sell)
                if (this.commission != null && this.commission!! != 0.0) {
                    stagingTransactions.add(
                        StagingTransaction(
                            platform = Platform.IBKR,
                            transactionType = TransactionType.FEE,
                            importStatus = ImportStatus.PENDING,
                            transactionDate = this.time.atOffset(ZoneOffset.UTC),
                            amount = abs(this.commission!!),
                            relatedStagingTransaction = sell
                        )
                    )
                }
            }
        }
        IBKRAction.WITHDRAWAL -> {
            stagingTransactions.add(
                StagingTransaction(
                    platform = Platform.IBKR,
                    transactionType = TransactionType.DEPOSIT,
                    importStatus = ImportStatus.PENDING,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    currency = currencies[this.currency],
                    amount = abs(this.amount!!),
                )
            )
        }
    }

    return stagingTransactions
}