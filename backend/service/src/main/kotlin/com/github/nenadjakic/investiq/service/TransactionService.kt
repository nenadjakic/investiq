package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.transaction.TransactionResponse
import com.github.nenadjakic.investiq.common.dto.transaction.BuyRequest
import com.github.nenadjakic.investiq.common.dto.transaction.SellRequest
import com.github.nenadjakic.investiq.common.dto.transaction.DepositRequest
import com.github.nenadjakic.investiq.common.dto.transaction.WithdrawalRequest
import com.github.nenadjakic.investiq.common.dto.transaction.DividendRequest
import com.github.nenadjakic.investiq.common.extension.toTransactionResponse
import com.github.nenadjakic.investiq.data.entity.transaction.Buy
import com.github.nenadjakic.investiq.data.entity.transaction.Deposit
import com.github.nenadjakic.investiq.data.entity.transaction.Dividend
import com.github.nenadjakic.investiq.data.entity.transaction.DividendAdjustment
import com.github.nenadjakic.investiq.data.entity.transaction.Fee
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.Sell
import com.github.nenadjakic.investiq.data.entity.transaction.Transaction
import com.github.nenadjakic.investiq.data.entity.transaction.Withdrawal
import com.github.nenadjakic.investiq.data.enum.TransactionType
import com.github.nenadjakic.investiq.data.repository.AssetRepository
import com.github.nenadjakic.investiq.data.repository.CurrencyRepository
import com.github.nenadjakic.investiq.data.repository.StagingTransactionRepository
import com.github.nenadjakic.investiq.data.repository.TransactionRepository
import jakarta.transaction.Transactional
import org.hibernate.Hibernate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID

@Service
class TransactionService(
    private val stagingTransactionRepository: StagingTransactionRepository,
    private val transactionRepository: TransactionRepository,
    private val assetRepository: AssetRepository,
    private val currencyRepository: CurrencyRepository,
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
                            it.price = BigDecimal.valueOf(stagingTransaction.price!!)
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
                            it.price = BigDecimal.valueOf(stagingTransaction.price!!)
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

                TransactionType.WITHDRAWAL -> {
                    val withdrawal = Withdrawal()
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
                    transactions.add(withdrawal)
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
                                        it.relatedTransaction = withdrawal
                                        it.currency = fee.currency!!
                                    }
                            )
                        }
                }
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
                            it.grossAmount = BigDecimal.valueOf(stagingTransaction.grossAmount!!)
                            it.amount = BigDecimal.valueOf(stagingTransaction.amount!!)
                            it.taxAmount = BigDecimal.valueOf(stagingTransaction.taxAmount!!)
                            it.taxPercentage = BigDecimal.valueOf(stagingTransaction.taxPercentage!!)
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

    @Transactional
    fun findAll(
        pageable: Pageable
    ): Page<TransactionResponse> {
        return transactionRepository.findAll(pageable)
            .map { Hibernate.unproxy(it, Transaction::class.java) }
            .map { it.toTransactionResponse() }
    }

    @Transactional
    fun findLast(n: Int): List<TransactionResponse> {
        val pageable = PageRequest.of(0, n, Sort.by(Sort.Direction.DESC, "date"))
        return transactionRepository.findAll(pageable)
            .map { Hibernate.unproxy(it, Transaction::class.java) }
            .map { it.toTransactionResponse() }
            .content
    }

    @Transactional
    fun addBuyTransaction(request: BuyRequest): UUID {
        val currency = currencyRepository.getReferenceById(request.currency!!)
        val transactions = mutableListOf<Transaction>()
        val buy = Buy().apply {
            this.platform = request.platform!!
            this.asset = assetRepository.getReferenceById(request.assetId!!)
            this.date = request.transactionDate!!.atZone(ZoneId.systemDefault()).toOffsetDateTime()
            this.quantity = request.quantity!!
            this.price = request.price!!
            this.currency = currency
        }
        transactions.add(buy)
        request.fee?.let {
            if (it > BigDecimal.ZERO) {
                val fee = Fee().apply {
                    this.platform = request.platform!!
                    this.date = request.transactionDate!!.atZone(ZoneId.systemDefault()).toOffsetDateTime()
                    this.amount = it
                    this.currency = currency
                    this.relatedTransaction = buy
                }
                transactions.add(fee)
            }
        }

        transactionRepository.saveAll(transactions)
        return buy.id!!
    }

    @Transactional
    fun addSellTransaction(request: SellRequest): UUID {
        val currency = currencyRepository.getReferenceById(request.currency!!)
        val transactions = mutableListOf<Transaction>()
        val sell = Sell().apply {
            this.platform = request.platform!!
            this.asset = assetRepository.getReferenceById(request.assetId!!)
            this.date = request.transactionDate!!.atZone(ZoneId.systemDefault()).toOffsetDateTime()
            this.quantity = request.quantity!!
            this.price = request.price!!
            this.currency = currency
        }
        transactions.add(sell)
        request.fee?.let {
            if (it > BigDecimal.ZERO) {
                val fee = Fee().apply {
                    this.platform = request.platform!!
                    this.date = request.transactionDate!!.atZone(ZoneId.systemDefault()).toOffsetDateTime()
                    this.amount = it
                    this.currency = currency
                    this.relatedTransaction = sell
                }
                transactions.add(fee)
            }
        }

        transactionRepository.saveAll(transactions)
        return sell.id!!
    }

    @Transactional
    fun addDepositTransaction(request: DepositRequest): UUID {
        val currency = currencyRepository.getReferenceById(request.currency!!)
        val transactions = mutableListOf<Transaction>()
        val deposit = Deposit().apply {
            this.platform = request.platform!!
            this.date = request.transactionDate!!.atZone(ZoneId.systemDefault()).toOffsetDateTime()
            this.amount = request.amount!!
            this.currency = currency
        }
        transactions.add(deposit)
        request.fee?.let {
            if (it > BigDecimal.ZERO) {
                val fee = Fee().apply {
                    this.platform = request.platform!!
                    this.date = request.transactionDate!!.atZone(ZoneId.systemDefault()).toOffsetDateTime()
                    this.amount = it
                    this.currency = currency
                    this.relatedTransaction = deposit
                }
                transactions.add(fee)
            }
        }

        transactionRepository.saveAll(transactions)
        return deposit.id!!
    }

    @Transactional
    fun addWithdrawalTransaction(request: WithdrawalRequest): UUID {
        val currency = currencyRepository.getReferenceById(request.currency!!)
        val transactions = mutableListOf<Transaction>()
        val withdrawal = Withdrawal().apply {
            this.platform = request.platform!!
            this.date = request.transactionDate!!.atZone(ZoneId.systemDefault()).toOffsetDateTime()
            this.amount = request.amount!!
            this.currency = currency
        }
        transactions.add(withdrawal)
        request.fee?.let {
            if (it > BigDecimal.ZERO) {
                val fee = Fee().apply {
                    this.platform = request.platform!!
                    this.date = request.transactionDate!!.atZone(ZoneId.systemDefault()).toOffsetDateTime()
                    this.amount = it
                    this.currency = currency
                    this.relatedTransaction = withdrawal
                }
                transactions.add(fee)
            }
        }

        transactionRepository.saveAll(transactions)
        return withdrawal.id!!
    }

    @Transactional
    fun addDividendTransaction(request: DividendRequest): UUID {
        val currency = currencyRepository.getReferenceById(request.currency!!)
        val transactions = mutableListOf<Transaction>()
        val dividend = Dividend().apply {
            this.platform = request.platform!!
            this.asset = assetRepository.getReferenceById(request.assetId!!)
            this.date = request.transactionDate!!.atZone(ZoneId.systemDefault()).toOffsetDateTime()
            this.grossAmount = request.grossAmount!!
            this.taxAmount = request.taxAmount!!
            this.taxPercentage = request.taxPercentage!!
            this.amount = request.getNetAmount()!!
            this.currency = currency
        }
        transactions.add(dividend)

        transactionRepository.saveAll(transactions)
        return dividend.id!!
    }
}