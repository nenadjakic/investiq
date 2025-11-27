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
            entries.find {
                it.csvAction.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException(
                "Unknown action '$value'"
            )
    }
}