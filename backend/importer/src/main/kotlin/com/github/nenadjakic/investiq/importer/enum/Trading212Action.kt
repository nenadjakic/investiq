package com.github.nenadjakic.investiq.importer.enum

import com.github.nenadjakic.investiq.data.enum.TransactionType

enum class Trading212Action(private val csvAction: String) {
    DIVIDEND("Dividend (Dividend)"),
    DEPOSIT("Deposit"),
    BUY("Market buy"),
    SELL("Market sell"),
    DIVIDEND_ADJUSTMENT("Dividend Adjustment"),
    WITHDRAWAL("Withdrawal");

    companion object {
        fun fromValue(value: String): Trading212Action =
            Trading212Action.values().find {
                it.csvAction.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException(
                "Unknown action '$value'"
            )
    }
}

fun Trading212Action.toTransactionType(): TransactionType {
    return when (this) {
        Trading212Action.BUY -> TransactionType.BUY
        Trading212Action.SELL -> TransactionType.SELL
        Trading212Action.DIVIDEND -> TransactionType.DIVIDEND
        Trading212Action.DEPOSIT -> TransactionType.DEPOSIT
        else -> TransactionType.UNKNOWN
    }
}