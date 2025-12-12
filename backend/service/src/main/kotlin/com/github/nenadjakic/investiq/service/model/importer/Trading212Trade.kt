package com.github.nenadjakic.investiq.service.model.importer

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.AssetAlias
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Tag
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.enum.TransactionType
import com.github.nenadjakic.investiq.common.enum.Trading212Action
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.collections.get
import kotlin.math.abs

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
    val withholdingCurrency: String?,
    val stampDuty: Double,
    val stampCurrency: String?,
    val fxFee: Double,
    val fxFeeCurrency: String?,
    val frTax: Double,
    val frTaxCurrency: String?
)

fun Trading212Trade.toStagingTransactions(
    assetAliases: Collection<AssetAlias>,
    currencies: Map<String, Currency>,
    tags: MutableMap<String, Tag>,

    ): Collection<StagingTransaction> {

    fun getFeeTransactions(
        parentStagingTransaction: StagingTransaction,
    ): Collection<StagingTransaction> {
        val fees = mutableListOf<StagingTransaction>()
        if (this.fxFee != 0.0) {
            fees.add(
                StagingTransaction(
                    platform = Platform.TRADING212,
                    transactionType = TransactionType.FEE,
                    externalId = this.id,
                    importStatus = ImportStatus.PENDING,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    currency = currencies[this.fxFeeCurrency],
                    amount = this.fxFee,
                    notes = this.notes,
                    relatedStagingTransaction = parentStagingTransaction
                ).also {
                    tags["Conversation fee"]?.let { element -> it.tags.add(element) }
                }
            )
        }
        if (this.stampDuty != 0.0) {
            fees.add(
                StagingTransaction(
                    platform = Platform.TRADING212,
                    transactionType = TransactionType.FEE,
                    externalId = this.id,
                    importStatus = ImportStatus.PENDING,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    currency = currencies[this.stampCurrency],
                    amount = this.stampDuty,
                    notes = this.notes,
                    relatedStagingTransaction = parentStagingTransaction
                ).also {
                    tags["Stamp duty reserve tax"]?.let { element -> it.tags.add(element) }
                }
            )
        }

        if (this.frTax != 0.0) {
            fees.add(
                StagingTransaction(
                    platform = Platform.TRADING212,
                    transactionType = TransactionType.FEE,
                    externalId = this.id,
                    importStatus = ImportStatus.PENDING,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    currency = currencies[this.frTaxCurrency],
                    amount = this.frTax,
                    notes = this.notes,
                    relatedStagingTransaction = parentStagingTransaction
                ).also {
                    tags["French transaction tax"]?.let { element -> it.tags.add(element) }
                }
            )
        }
        return fees
    }

    val stagingTransactions = mutableListOf<StagingTransaction>()

    val asset: Asset?
    val assets = assetAliases.filter {
        it.platform == Platform.TRADING212 && it.externalSymbol.equals(
            this.ticker, ignoreCase = true
        )
    }

    asset = if (!assets.isEmpty()) {
        assets.first().asset
    } else {
        null
    }

    when (this.action) {
        Trading212Action.DEPOSIT -> {
            val deposit = StagingTransaction(
                platform = Platform.TRADING212,
                transactionType = TransactionType.DEPOSIT,
                externalId = this.id,
                importStatus = ImportStatus.PENDING,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                currency = currencies[this.currencyTotal],
                amount = this.total,
                notes = this.notes,
            )
            stagingTransactions.add(deposit)
            stagingTransactions.addAll(getFeeTransactions(deposit))
        }
        Trading212Action.MARKET_BUY, Trading212Action.LIMIT_BUY -> {
            val buy = StagingTransaction(
                platform = Platform.TRADING212,
                transactionType = TransactionType.BUY,
                externalId = this.id,
                importStatus = ImportStatus.PENDING,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                price = this.pricePerShare,
                quantity = this.numberOfShares,
                externalSymbol = this.ticker,
                resolvedAsset = asset,
            )
            stagingTransactions.add(buy)
            stagingTransactions.addAll(getFeeTransactions(buy))
        }
        Trading212Action.SELL -> {
            val buy = StagingTransaction(
                platform = Platform.TRADING212,
                transactionType = TransactionType.SELL,
                externalId = this.id,
                importStatus = ImportStatus.PENDING,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                price = this.pricePerShare,
                quantity = this.numberOfShares,
                externalSymbol = this.ticker,
                resolvedAsset = asset,
            )
            stagingTransactions.add(buy)
            stagingTransactions.addAll(getFeeTransactions(buy))
        }
        Trading212Action.DIVIDEND, Trading212Action.DIVIDEND_OTHER -> {
            stagingTransactions.add(
                StagingTransaction(
                    platform = Platform.TRADING212,
                    transactionType = TransactionType.DIVIDEND,
                    externalId = this.id,
                    importStatus = ImportStatus.PENDING,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    quantity = this.numberOfShares,
                    price = this.pricePerShare,
                    externalSymbol = this.ticker,
                    resolvedAsset = asset,
                    resolutionNote = null,
                    grossAmount = this.numberOfShares * this.pricePerShare,
                    taxAmount = this.withholdingTax,
                    currency = currencies[this.currencyPricePerShare],
                ).also {
                    it.amount = it.grossAmount!! - it.taxAmount!!
                    it.taxPercentage = it.taxAmount!! / (it.grossAmount!! - it.taxAmount!!) * 100
                }
            )
        }
        Trading212Action.DIVIDEND_ADJUSTMENT -> {
            stagingTransactions.add(
                StagingTransaction(
                    platform = Platform.TRADING212,
                    transactionType = TransactionType.DIVIDEND_ADJUSTMENT,
                    externalId = this.id,
                    importStatus = ImportStatus.PENDING,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    amount = abs(this.total),
                    currency = currencies[this.currencyTotal],
                )
            )
        }
        Trading212Action.SPLIT_OPEN, Trading212Action.SPLIT_CLOSE -> {}
        else -> {
            val unknown = StagingTransaction(
                platform = Platform.TRADING212,
                transactionType = TransactionType.UNKNOWN,
                quantity = this.numberOfShares,
                price = this.pricePerShare,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                externalSymbol = this.ticker,
                resolvedAsset = asset,
                importStatus = ImportStatus.PENDING,
                externalId = this.id
            )
            stagingTransactions.add(unknown)
            stagingTransactions.addAll(getFeeTransactions(unknown))
        }
    }
    return stagingTransactions
}