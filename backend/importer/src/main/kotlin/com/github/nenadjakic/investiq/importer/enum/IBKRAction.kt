package com.github.nenadjakic.investiq.importer.enum

enum class IBKRAction(private val csvLabel: String) {
    DIVIDEND("DIVIDEND"),
    DEPOSIT("DEPOSIT"),
    BUY("BUY"),
    SELL("SELL"),
    WITHDRAWAL("WITHDRAWAL");
}