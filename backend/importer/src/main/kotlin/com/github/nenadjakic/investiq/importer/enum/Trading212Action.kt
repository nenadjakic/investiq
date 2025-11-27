package com.github.nenadjakic.investiq.importer.enum

enum class Trading212Action(private val csvAction: String) {
    DIVIDEND("Dividend (Dividend)"),
    DIVIDEND_OTHER("Dividend (Return of capital)"),
    DEPOSIT("Deposit"),
    MARKET_BUY("Market buy"),
    LIMIT_BUY("Limit buy"),
    SELL("Market sell"),
    DIVIDEND_ADJUSTMENT("Dividend Adjustment"),
    WITHDRAWAL("Withdrawal"),
    SPLIT_OPEN("Stock split open"),
    SPLIT_CLOSE("Stock split close");

    companion object {
        fun fromValue(value: String): Trading212Action =
            entries.find {
                it.csvAction.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException(
                "Unknown action '$value'"
            )
    }
}