package com.github.nenadjakic.investiq.importer.enum

import com.github.nenadjakic.investiq.importer.enum.Trading212Action.entries

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