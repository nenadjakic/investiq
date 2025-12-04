package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.data.entity.Action
import com.github.nenadjakic.investiq.data.entity.transaction.Buy
import com.github.nenadjakic.investiq.data.entity.transaction.Sell
import com.github.nenadjakic.investiq.data.entity.transaction.Transaction
import com.github.nenadjakic.investiq.data.repository.ActionRepository
import com.github.nenadjakic.investiq.data.repository.BuyRepository
import com.github.nenadjakic.investiq.data.repository.SellRepository
import com.github.nenadjakic.investiq.data.repository.TransactionRepository
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZoneOffset

@Service
class ActionService(
    private val actionRepository: ActionRepository,
    private val transactionRepository: TransactionRepository,
    private val buyRepository: BuyRepository,
    private val sellRepository: SellRepository

) {
    @Scheduled(fixedDelayString = "PT1H")
    @Transactional
    fun execute() {
        val actions = mutableListOf<Action>()
        val transactions = mutableListOf<Transaction>()
        actionRepository.findAllByExecutedFalseAndDateLessThanEqual(LocalDate.now()).forEach { action ->
            val factor = try {
                parseFactor(action.rule)
            } catch (ex: IllegalArgumentException) {
                return@forEach
            }

            val buys = buyRepository.findAllByAssetAndDateLessThanEqual(
                action.asset,
                action.date.atStartOfDay().atOffset(ZoneOffset.UTC)
            )

            val sells = sellRepository.findAllByAssetAndDateLessThanEqual(
                action.asset,
                action.date.atStartOfDay().atOffset(ZoneOffset.UTC)
            )

            val allTransactions = buys + sells

            allTransactions.forEach { transaction ->
                when (transaction) {
                    is Buy, is Sell -> {
                        applyFactor(transaction, factor)
                        transactions.add(transaction)
                    }

                    else -> return@forEach
                }
            }
            action.executed = true
            actions.add(action)
        }
        transactionRepository.saveAll(transactions)
        actionRepository.saveAll(actions)
    }

    private fun parseFactor(rule: String): BigDecimal {
        val regex = "(SPLIT)_(\\d+)_FOR_(\\d+)".toRegex(RegexOption.IGNORE_CASE)
        val match = regex.matchEntire(rule)
            ?: throw IllegalArgumentException("Invalid rule format: $rule")

        val (_, toStr, fromStr) = match.destructured
        val to = BigDecimal(toStr)
        val from = BigDecimal(fromStr)

        if (from.compareTo(BigDecimal.ZERO) == 0 || to.compareTo(BigDecimal.ZERO) == 0) {
            throw IllegalArgumentException("From/To cannot be zero")
        }

        return to.divide(from, 12, RoundingMode.HALF_UP)
    }

    private fun applyFactor(transaction: Transaction, factor: BigDecimal) {
        when (transaction) {
            is Buy -> {
                transaction.quantity = transaction.quantity.multiply(factor)
                transaction.price = transaction.price.divide(factor, 8, RoundingMode.HALF_UP)
            }

            is Sell -> {
                transaction.quantity = transaction.quantity.multiply(factor)
                transaction.price = transaction.price.divide(factor, 8, RoundingMode.HALF_UP)
            }

            else -> return
        }
    }
}