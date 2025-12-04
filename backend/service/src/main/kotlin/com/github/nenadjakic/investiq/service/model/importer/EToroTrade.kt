package com.github.nenadjakic.investiq.service.model.importer

import com.github.nenadjakic.investiq.service.CurrencyHistoryService
import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.AssetAlias
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Tag
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.enum.TransactionType
import com.github.nenadjakic.investiq.common.enum.EToroAction
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs

data class EToroTrade (
    val action: EToroAction,
    val time: LocalDateTime,
    val ticker: String,
    val details: String,
    val amount: Double,
    val units: Double,
    val id: String,
    var dividend: EToroDividend? = null,
    var fees: MutableList<EToroFee> = mutableListOf()
)

data class EToroDividend (
    val time: LocalDate,
    val amount: Double,
    val withHoldingTaxAmount: Double,
    val withHoldingTaxRate: Double,
    var parentId: String
)

data class EToroFee (
    val time: LocalDateTime,
    val amount: Double,
)

fun EToroTrade.toStagingTransactions(
    assetAliases: Collection<AssetAlias>,
    currencies: Map<String, Currency>,
    tags: MutableMap<String, Tag>,
    currencyHistoryService: CurrencyHistoryService,
    ): Collection<StagingTransaction> {
    val extractDepositCurrency: (String) -> String = { text ->
        Regex("""\b([A-Z]{2,5})\b""")
            .find(text)!!.value
    }
    val extractBuySellCurrency: (String) -> String = {
        it.substringAfter("/")
    }

    val stagingTransactions = mutableListOf<StagingTransaction>()

    val asset: Asset?
    val assets = assetAliases.filter {
        it.platform == Platform.ETORO && it.externalSymbol.equals(
            this.ticker, ignoreCase = true
        )
    }

    asset = if (!assets.isEmpty()) {
        assets.first().asset
    } else {
        null
    }

    when (this.action) {
        EToroAction.DEPOSIT -> {
            val deposit = StagingTransaction(
                platform = Platform.ETORO,
                transactionType = TransactionType.DEPOSIT,
                externalId = this.id,
                importStatus = ImportStatus.PENDING,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                currency = currencies[extractDepositCurrency(this.details)],
                amount = this.amount,
                notes = this.details,
            )
            stagingTransactions.add(deposit)
        }
        EToroAction.DEPOSIT_CONVERSETION_FEE -> StagingTransaction(
            platform = Platform.ETORO,
            transactionType = TransactionType.FEE,
            externalId = this.id,
            importStatus = ImportStatus.PENDING,
            transactionDate = this.time.atOffset(ZoneOffset.UTC),
            currency = currencies[extractDepositCurrency(this.details)],
        )
        EToroAction.SDRT_FEE -> TODO()
        EToroAction.FEE -> TODO()
        EToroAction.BUY -> {
            val buy = StagingTransaction(
                platform = Platform.ETORO,
                transactionType = TransactionType.BUY,
                externalId = this.id,
                importStatus = ImportStatus.PENDING,
                transactionDate = this.time.atOffset(ZoneOffset.UTC),
                currency = currencies["USD"],
                resolvedAsset = asset,
                externalSymbol = this.ticker,
                quantity = this.units,
                amount = this.amount,
                notes = this.details
            ).also {
                val rawCurrency = extractBuySellCurrency(this.details).substringAfter("/")
                val targetCurrency = if (rawCurrency == "GBX") "GBP" else rawCurrency

                var converted =
                    currencyHistoryService.convert(
                        BigDecimal.valueOf(this.amount),
                        "USD",
                        targetCurrency,
                        it.transactionDate.toLocalDate()
                    ).toDouble()

                if (rawCurrency == "GBX") {
                    converted *= 100
                }

                it.price = converted / this.units
                it.currency = currencies[rawCurrency]
            }
            stagingTransactions.add(buy)
            this.fees.forEach {
                stagingTransactions.add(
                    StagingTransaction(
                        platform = Platform.ETORO,
                        transactionType = TransactionType.FEE,
                        externalId = this.id,
                        importStatus = ImportStatus.PENDING,
                        transactionDate = it.time.atOffset(ZoneOffset.UTC),
                        currency = currencies["USD"],
                        amount = abs(it.amount),
                        relatedStagingTransaction = buy
                    )
                )
            }
        }
        EToroAction.SELL -> {
            stagingTransactions.add(
                StagingTransaction(
                    platform = Platform.ETORO,
                    transactionType = TransactionType.SELL,
                    externalId = this.id,
                    importStatus = ImportStatus.PENDING,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    currency = currencies["USD"],
                    resolvedAsset = asset,
                    externalSymbol = this.ticker,
                    quantity = this.units,
                    amount = abs(this.amount),
                    notes = extractBuySellCurrency(this.details)
                ).also {
                    val rawCurrency = extractBuySellCurrency(this.details).substringAfter("/")
                    val targetCurrency = if (rawCurrency == "GBX") "GBP" else rawCurrency

                    var converted =
                        currencyHistoryService.convert(
                            BigDecimal.valueOf(this.amount),
                            "USD",
                            targetCurrency,
                            it.transactionDate.toLocalDate()
                        ).toDouble()

                    if (rawCurrency == "GBX") {
                        converted *= 100
                    }

                    it.price = converted / this.units
                    it.currency = currencies[rawCurrency]
                }
            )
        }
        EToroAction.DIVIDEND -> {
            stagingTransactions.add(
                StagingTransaction(
                    platform = Platform.ETORO,
                    transactionType = TransactionType.DIVIDEND,
                    externalId = this.id,
                    importStatus = ImportStatus.PENDING,
                    transactionDate = this.time.atOffset(ZoneOffset.UTC),
                    currency = currencies["USD"],
                    externalSymbol = this.ticker,
                    resolvedAsset = asset,
                    notes = this.details
                ).also {
                    val dividend = this.dividend!!
                    it.taxAmount = dividend.withHoldingTaxAmount
                    it.taxPercentage = dividend.withHoldingTaxRate
                    it.grossAmount = dividend.amount + dividend.withHoldingTaxAmount
                    it.amount = dividend.amount
                }
            )
        }
        EToroAction.DIVIDEND_ADJUSTMENT -> {}
        EToroAction.WITHDRAWAL -> {}
    }

    return stagingTransactions
}