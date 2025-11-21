package com.github.nenadjakic.investiq.importer.model

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.AssetAlias
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.importer.enum.Trading212Action
import com.github.nenadjakic.investiq.importer.enum.toTransactionType
import java.time.LocalDateTime
import java.time.ZoneOffset

data class Trading212Trade(
    val action: Trading212Action,
    val time: LocalDateTime,
    val isin: String,
    val ticker: String,
    val name: String,
    val notes: String?,
    val id: String,
    val numberOfShares: Double,
    val pricePerShare: Double,
    val currencyPricePerShare: String,
    val exchangeRate: Double,
    val currencyResult: String,
    val total: Double,
    val currencyTotal: String,
    val withholdingTax: Double,
    val withholdingCurrency: String,
    val stampDuty: Double,
    val stampCurrency: String,
    val fxFee: Double,
    val fxFeeCurrency: String,
    val frTax: Double,
    val frTaxCurrency: String
)

fun Trading212Trade.toStagingTransactions(assetAliases: Collection<AssetAlias>): Collection<StagingTransaction> {
    var stagingTransactions = mutableListOf<StagingTransaction>()

    val asset: Asset?
    val assets = assetAliases.filter {
        it.platform == Platform.TRADING212 && it.externalSymbol.equals(
            this.ticker,
            ignoreCase = true
        )
    }

    if (!assets.isEmpty()) {
        asset = assets.first().asset
    } else {
        asset = null
    }

    when (this.action) {
        Trading212Action.BUY -> {
            stagingTransactions.add(StagingTransaction(
                id = null,
                transactionType = this.action.toTransactionType(),
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                description = null,
                price = this.pricePerShare,
                quantity = this.numberOfShares,
                notes = null,
                externalSymbol = this.ticker,
                resolvedAsset = asset,
                resolutionNote = null,
                importStatus = ImportStatus.PENDING
            ))
        }
        Trading212Action.DEPOSIT -> {

        }
        else -> {
            stagingTransactions.add(
                StagingTransaction(
                    id = null,
                    transactionType = this.action.toTransactionType(),
                    quantity = this.numberOfShares,
                    price = this.pricePerShare,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    notes = null,
                    externalSymbol = this.ticker,
                    resolvedAsset = asset,
                    resolutionNote = null,
                    importStatus = ImportStatus.PENDING
                )
            )
        }
    }
    return stagingTransactions
}