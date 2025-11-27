package com.github.nenadjakic.investiq.importer.enum

enum class EToroAction(private val xlsxValue: String) {
    DEPOSIT("Deposit"),
    DEPOSIT_CONVERSETION_FEE("Deposit Conversion Fee"),
    SDRT_FEE("SDRT"),
    FEE("Commission"),
    BUY("Open Position"),
    SELL("Market sell"),
    DIVIDEND("Dividend"),
    DIVIDEND_ADJUSTMENT("Dividend Adjustment"),
    WITHDRAWAL("Withdrawal");

    companion object {
        fun fromValue(value: String): EToroAction =
            EToroAction.entries.find {
                it.xlsxValue.equals(value, ignoreCase = true)
            } ?: throw IllegalArgumentException(
                "Unknown action '$value'"
            )
    }
}