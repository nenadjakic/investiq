package com.github.nenadjakic.investiq.common.enum

enum class RevolutAction(private val csvLabel: String) {
    DIVIDEND("DIVIDEND"),
    DEPOSIT("CASH TOP-UP"),
    MARKET_BUY("BUY - MARKET"),
    MARKET_SELL("SELL - MARKET"),
    WITHDRAWAL("WITHDRAWAL");

    companion object {
        fun fromValue(value: String): RevolutAction =
            entries.find {
                it.csvLabel.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException(
                "Unknown action '$value'"
            )
    }
}