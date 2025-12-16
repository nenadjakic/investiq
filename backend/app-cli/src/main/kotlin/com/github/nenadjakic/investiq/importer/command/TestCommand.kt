package com.github.nenadjakic.investiq.importer.command

import com.github.nenadjakic.investiq.integration.service.FrankfurterService
import com.github.nenadjakic.investiq.integration.service.YahooFinanceCurrencyService
import com.github.nenadjakic.investiq.service.TransactionService
import org.springframework.shell.standard.ShellCommandGroup
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import java.time.LocalDate

@ShellCommandGroup("Asset commands")
@ShellComponent
class TestCommand(
    private val transactionService: TransactionService,
    private val frankfurterService: FrankfurterService,
    private val yahooFinanceCurrencyFetcher: YahooFinanceCurrencyService

) {

    @ShellMethod
    fun copy() {
        transactionService.copy()
    }

    @ShellMethod
    fun fetchCurrency() {
        val x = frankfurterService.convert("EUR", "USD")
        println(x)
    }

    @ShellMethod
    fun fetchCurYahoo() {
        val x = yahooFinanceCurrencyFetcher.fetchHistory(
            "USD",
            "EUR",
            LocalDate.of(2025,1,1),
            LocalDate.of(2025,10,31)
        )
        println(x)
    }
}