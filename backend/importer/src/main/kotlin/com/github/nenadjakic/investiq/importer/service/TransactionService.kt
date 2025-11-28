package com.github.nenadjakic.investiq.importer.service

import com.github.nenadjakic.investiq.currencyfetcher.service.FrankfurterService
import com.github.nenadjakic.investiq.data.entity.transaction.Buy
import com.github.nenadjakic.investiq.data.entity.transaction.Deposit
import com.github.nenadjakic.investiq.data.entity.transaction.Dividend
import com.github.nenadjakic.investiq.data.entity.transaction.DividendAdjustment
import com.github.nenadjakic.investiq.data.entity.transaction.Fee
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.Sell
import com.github.nenadjakic.investiq.data.entity.transaction.Transaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.enum.TransactionType
import com.github.nenadjakic.investiq.data.repository.StagingTransactionRepository
import com.github.nenadjakic.investiq.data.repository.TransactionRepository
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class TransactionService(
    private val currencyHistoryService: CurrencyHistoryService,
    private val stagingTransactionRepository: StagingTransactionRepository,
    private val transactionRepository: TransactionRepository
) {

    @Scheduled(fixedDelayString = "PT1H")
    @Transactional
    fun copy() {
        val transactions = mutableListOf<Transaction>()
        val stagingTransactions =
            stagingTransactionRepository.findAllByImportStatusAndRelatedStagingTransactionIsNull(ImportStatus.VALIDATED)

        stagingTransactions.forEach { stagingTransaction ->
            val related = stagingTransaction.relatedStagingTransactions

            stagingTransaction.importStatus = ImportStatus.IMPORTED

            when (stagingTransaction.transactionType) {
                TransactionType.BUY -> {
                    val buy = Buy()
                        .apply {
                            this.platform = stagingTransaction.platform
                            this.asset = stagingTransaction.resolvedAsset!!
                            this.date = stagingTransaction.transactionDate
                            this.tags = stagingTransaction.tags.toMutableSet()
                            this.externalId = stagingTransaction.externalId
                        }
                        .also {
                            it.quantity = BigDecimal.valueOf(stagingTransaction.quantity!!)
                            it.currency = stagingTransaction.resolvedAsset!!.currency
                        }
                        .also {
                            if (it.platform == Platform.ETORO) {
                                it.price =
                                    currencyHistoryService
                                        .convert(
                                            BigDecimal.valueOf(stagingTransaction.amount!!),
                                            "USD",
                                            "EUR",
                                            it.date.toLocalDate()
                                        )
                                        .divide(it.quantity, 10, RoundingMode.HALF_UP)
                            } else {
                                it.price = BigDecimal.valueOf(stagingTransaction.price!!)
                            }
                        }
                    transactions.add(buy)
                    related
                        .filter { it.transactionType == TransactionType.FEE }
                        .forEach { fee ->
                            fee.importStatus = ImportStatus.IMPORTED
                            transactions.add(
                                Fee()
                                    .apply {
                                        this.platform = fee.platform
                                        this.date = fee.transactionDate
                                        this.tags = fee.tags.toMutableSet()
                                        this.externalId = fee.externalId
                                    }
                                    .also {
                                        it.amount = BigDecimal.valueOf(fee.amount!!)
                                        it.relatedTransaction = buy
                                        it.currency = fee.currency!!
                                    }
                            )
                        }

                }

                TransactionType.SELL -> {
                    val sell = Sell()
                        .apply {
                            this.platform = stagingTransaction.platform
                            this.asset = stagingTransaction.resolvedAsset!!
                            this.date = stagingTransaction.transactionDate
                            this.tags = stagingTransaction.tags.toMutableSet()
                            this.externalId = stagingTransaction.externalId
                        }
                        .also {
                            it.quantity = BigDecimal.valueOf(stagingTransaction.quantity!!)
                            it.currency = stagingTransaction.resolvedAsset!!.currency
                        }
                        .also {
                            if (it.platform == Platform.ETORO) {
                                it.price =
                                    currencyHistoryService
                                        .convert(
                                            BigDecimal.valueOf(stagingTransaction.amount!!),
                                            "USD",
                                            "EUR",
                                            it.date.toLocalDate()
                                        )
                                        .divide(it.quantity, 10, RoundingMode.HALF_UP)
                            } else {
                                it.price = BigDecimal.valueOf(stagingTransaction.price!!)
                            }
                        }
                    transactions.add(sell)
                    related
                        .filter { it.transactionType == TransactionType.FEE }
                        .forEach { fee ->
                            fee.importStatus = ImportStatus.IMPORTED
                            transactions.add(
                                Fee()
                                    .apply {
                                        this.platform = fee.platform
                                        this.date = fee.transactionDate
                                        this.tags = fee.tags.toMutableSet()
                                        this.externalId = fee.externalId
                                    }
                                    .also {
                                        it.amount = BigDecimal.valueOf(fee.amount!!)
                                        it.relatedTransaction = sell
                                        it.currency = fee.currency!!
                                    }
                            )
                        }
                }

                TransactionType.FEE -> {}
                TransactionType.DEPOSIT -> {
                    val deposit = Deposit()
                        .apply {
                            this.platform = stagingTransaction.platform
                            this.date = stagingTransaction.transactionDate
                            this.tags = stagingTransaction.tags.toMutableSet()
                            this.externalId = stagingTransaction.externalId
                        }
                        .also {
                            it.amount = BigDecimal.valueOf(stagingTransaction.amount!!)
                            it.currency = stagingTransaction.currency!!
                        }
                    transactions.add(deposit)
                    related
                        .filter { it.transactionType == TransactionType.FEE }
                        .forEach { fee ->
                            fee.importStatus = ImportStatus.IMPORTED
                            transactions.add(
                                Fee()
                                    .apply {
                                        this.platform = fee.platform
                                        this.date = fee.transactionDate
                                        this.tags = fee.tags.toMutableSet()
                                        this.externalId = fee.externalId
                                    }
                                    .also {
                                        it.amount = BigDecimal.valueOf(fee.amount!!)
                                        it.relatedTransaction = deposit
                                        it.currency = fee.currency!!
                                    }
                            )
                        }
                }

                TransactionType.WITHDRAWAL -> TODO()
                TransactionType.DIVIDEND -> {
                    val dividend = Dividend()
                        .apply {
                            this.platform = stagingTransaction.platform
                            this.asset = stagingTransaction.resolvedAsset!!
                            this.date = stagingTransaction.transactionDate
                            this.tags = stagingTransaction.tags.toMutableSet()
                            this.externalId = stagingTransaction.externalId
                        }
                        .also {
                            it.grossAmount =
                                BigDecimal.valueOf(stagingTransaction.price!! * stagingTransaction.quantity!!)
                            it.taxAmount = BigDecimal.valueOf(stagingTransaction.taxAmount!!)
                            it.taxPercentage = it.taxAmount!!
                                .divide(it.grossAmount, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal(100))
                                .setScale(2, RoundingMode.HALF_UP)
                            it.currency = stagingTransaction.currency!!
                        }
                    transactions.add(dividend)
                }

                TransactionType.DIVIDEND_ADJUSTMENT -> {
                    val dividendAdjustment = DividendAdjustment()
                        .apply {
                            this.platform = stagingTransaction.platform
                            this.date = stagingTransaction.transactionDate
                            this.tags = stagingTransaction.tags.toMutableSet()
                            this.externalId = stagingTransaction.externalId
                        }.also {
                            it.amount = BigDecimal.valueOf(stagingTransaction.amount!!)
                            it.currency = stagingTransaction.currency!!
                        }
                    transactions.add(dividendAdjustment)
                }

                TransactionType.UNKNOWN -> {}
            }
        }

        transactionRepository.saveAll(transactions)
    }
}