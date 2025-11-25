package com.github.nenadjakic.investiq.importer.service

import com.github.nenadjakic.investiq.data.entity.transaction.Buy
import com.github.nenadjakic.investiq.data.entity.transaction.Deposit
import com.github.nenadjakic.investiq.data.entity.transaction.Dividend
import com.github.nenadjakic.investiq.data.entity.transaction.DividendAdjustment
import com.github.nenadjakic.investiq.data.entity.transaction.Fee
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.Sell
import com.github.nenadjakic.investiq.data.entity.transaction.Transaction
import com.github.nenadjakic.investiq.data.enum.TransactionType
import com.github.nenadjakic.investiq.data.repository.CurrencyRepository
import com.github.nenadjakic.investiq.data.repository.StagingTransactionRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class TransactionService(
    private val stagingTransactionRepository: StagingTransactionRepository,
    private val currencyRepository: CurrencyRepository
) {

    @Transactional
    fun copy() {
        currencyRepository.findAll().associateBy { it.code!! }
        val transactions = mutableListOf<Transaction>()
        val stagingTransactions = stagingTransactionRepository.findAllByImportStatusAndRelatedStagingTransactionIsNull(ImportStatus.VALIDATED)

        stagingTransactions.forEach { stagingTransaction ->
            when (stagingTransaction.transactionType) {
                TransactionType.BUY -> {
                    val buy = Buy()
                        .apply {
                            this.platform = stagingTransaction.platform
                            this.asset = stagingTransaction.resolvedAsset!!
                            this.date = stagingTransaction.transactionDate
                            this.tags = stagingTransaction.tags
                            this.externalId = stagingTransaction.externalId
                        }
                        .also {
                            it.quantity = BigDecimal.valueOf(stagingTransaction.quantity!!)
                            it.price = BigDecimal.valueOf(stagingTransaction.price!!)
                        }
                    transactions.add(buy)
                    if (stagingTransaction.relatedStagingTransactions.isNotEmpty()) {
                        stagingTransaction.relatedStagingTransactions
                            .filter { it.transactionType == TransactionType.FEE }
                            .forEach { stagingTransaction ->
                                transactions.add(
                                    Fee()
                                        .apply {
                                            this.platform = stagingTransaction.platform
                                            this.date = stagingTransaction.transactionDate
                                            this.tags = stagingTransaction.tags
                                            this.externalId = stagingTransaction.externalId
                                        }
                                        .also {
                                            it.priceAmount = BigDecimal.valueOf(stagingTransaction.amount!!)
                                            it.relatedTransaction = buy
                                            it.currency = stagingTransaction.currency!!
                                        }
                                )
                            }
                    }
                }
                TransactionType.SELL -> {
                    val sell = Sell()
                        .apply {
                            this.platform = stagingTransaction.platform
                            this.asset = stagingTransaction.resolvedAsset!!
                            this.date = stagingTransaction.transactionDate
                            this.tags = stagingTransaction.tags
                            this.externalId = stagingTransaction.externalId
                        }
                        .also {
                            it.quantity = BigDecimal.valueOf(stagingTransaction.quantity!!)
                            it.price = BigDecimal.valueOf(stagingTransaction.price!!)
                        }
                    transactions.add(sell)
                    if (stagingTransaction.relatedStagingTransactions.isNotEmpty()) {
                        stagingTransaction.relatedStagingTransactions
                            .filter { it.transactionType == TransactionType.FEE }
                            .forEach { stagingTransaction ->
                                transactions.add(
                                    Fee()
                                        .apply {
                                            this.platform = stagingTransaction.platform
                                            this.date = stagingTransaction.transactionDate
                                            this.tags = stagingTransaction.tags
                                            this.externalId = stagingTransaction.externalId
                                        }
                                        .also {
                                            it.priceAmount = BigDecimal.valueOf(stagingTransaction.amount!!)
                                            it.relatedTransaction = sell
                                            it.currency = stagingTransaction.currency!!
                                        }
                                )
                            }
                    }
                }
                TransactionType.FEE -> {}
                TransactionType.DEPOSIT -> {
                    val deposit = Deposit()
                        .apply {
                            this.platform = stagingTransaction.platform
                            this.date = stagingTransaction.transactionDate
                            this.tags = stagingTransaction.tags
                            this.externalId = stagingTransaction.externalId
                        }
                        .also {
                            it.amount = BigDecimal.valueOf(stagingTransaction.amount!!)
                            it.currency = stagingTransaction.currency!!
                        }
                    transactions.add(deposit)
                    if (stagingTransaction.relatedStagingTransactions.isNotEmpty()) {
                        stagingTransaction.relatedStagingTransactions
                            .filter { it.transactionType == TransactionType.FEE }
                            .forEach { stagingTransaction ->
                                transactions.add(
                                    Fee()
                                        .apply {
                                            this.platform = stagingTransaction.platform
                                            this.date = stagingTransaction.transactionDate
                                            this.tags = stagingTransaction.tags
                                            this.externalId = stagingTransaction.externalId
                                        }
                                        .also {
                                            it.priceAmount = BigDecimal.valueOf(stagingTransaction.amount!!)
                                            it.relatedTransaction = deposit
                                            it.currency = stagingTransaction.currency!!
                                        }
                                )
                            }
                    }
                }
                TransactionType.WITHDRAWAL -> TODO()
                TransactionType.DIVIDEND -> {
                    val dividend = Dividend()
                        .apply {
                            this.platform = stagingTransaction.platform
                            this.asset = stagingTransaction.resolvedAsset!!
                            this.date = stagingTransaction.transactionDate
                            this.tags = stagingTransaction.tags
                            this.externalId = stagingTransaction.externalId
                        }
                        .also {
                            it.grossAmount = BigDecimal.valueOf(stagingTransaction.grossAmount!!)
                            it.taxAmount = BigDecimal.valueOf(stagingTransaction.amount!!)
                            it.taxPercentage = it.taxAmount!!
                                .divide(it.grossAmount, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal(100))
                                .setScale(2, RoundingMode.HALF_UP)
                            it.currency = stagingTransaction.currency!!
                        }
                    transactions.add(dividend)
                    if (stagingTransaction.relatedStagingTransactions.isNotEmpty()) {
                        stagingTransaction.relatedStagingTransactions
                            .filter { it.transactionType == TransactionType.FEE }
                            .forEach { stagingTransaction ->
                                transactions.add(
                                    Fee()
                                        .apply {
                                            this.platform = stagingTransaction.platform
                                            this.date = stagingTransaction.transactionDate
                                            this.tags = stagingTransaction.tags
                                            this.externalId = stagingTransaction.externalId
                                        }
                                        .also {
                                            it.priceAmount = BigDecimal.valueOf(stagingTransaction.amount!!)
                                            it.relatedTransaction = dividend
                                            it.currency = stagingTransaction.currency!!
                                        }
                                )
                            }
                    }
                }
                TransactionType.DIVIDEND_ADJUSTMENT -> {
                    val dividendAdjustment = DividendAdjustment()
                        .apply {
                            this.platform = stagingTransaction.platform
                            this.date = stagingTransaction.transactionDate
                            this.tags = stagingTransaction.tags
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
    }
}