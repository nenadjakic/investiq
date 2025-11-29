package com.github.nenadjakic.investiq.importer.enum

enum class RevolutAction(private val csvLabel: String) {
    DIVIDEND("DIVIDEND"),
    DEPOSIT("CASH TOP-UP"),
    MARKET_BUY("BUY - MARKET"),
    MARKET_SELL("SELL - MARKET"),
    WITHDRAWAL("WITHDRAWAL");

}