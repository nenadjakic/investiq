package com.github.nenadjakic.investiq.scheduler

import com.github.nenadjakic.investiq.data.entity.history.CurrencyHistory
import com.github.nenadjakic.investiq.data.repository.CurrencyHistoryRepository
import com.github.nenadjakic.investiq.data.repository.CurrencyRepository
import com.github.nenadjakic.investiq.integration.dto.CurrencyHistoryList
import com.github.nenadjakic.investiq.integration.service.YahooFinanceCurrencyService
import com.github.nenadjakic.investiq.service.CurrencyHistoryService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CurrencyScheduler(
    private val currencyRepository: CurrencyRepository,
    private val currencyHistoryRepository: CurrencyHistoryRepository,
    private val currencyHistoryService: CurrencyHistoryService,
    private val yahooFinanceCurrencyService: YahooFinanceCurrencyService
) {
    val log: Logger = LoggerFactory.getLogger(javaClass)

    @Value($$"${investiq.scheduler.currency.from}")
    private lateinit var fromCurrencies: List<String>

    @Value($$"${investiq.scheduler.currency.to}")
    private lateinit var toCurrencies: List<String>

    @Value($$"${investiq.scheduler.currency.fetch-delay-ms}")
    private var fetchDelayMs: Long? = null

    @Scheduled(fixedDelayString = "PT168H")
    fun fetchCurrencyHistories() {
        val currencyHistories = mutableListOf<CurrencyHistory>()
        fromCurrencies.forEach { fromCurrency ->
            toCurrencies.forEach { toCurrency ->
                try {
                    var latestValidDate = currencyHistoryService.getLatestValidDate(fromCurrency, toCurrency)
                    if (latestValidDate == null) {
                        latestValidDate = currencyHistoryService.getLatestValidDate(toCurrency, fromCurrency)
                    }
                    val fromDate = latestValidDate ?: LocalDate.of(2024, 10, 1)
                    val toDate = LocalDate.now()

                    if (fromDate.equals(toDate)) {
                        return@forEach
                    }

                    val response = yahooFinanceCurrencyService.fetchHistory(fromCurrency, toCurrency, fromDate, toDate)

                    currencyHistories.addAll(initCurrencyHistories(response))
                    Thread.sleep(fetchDelayMs!!)
                } catch (ex: Exception) {
                    log.error("Error fetching data for $fromCurrency -> $toCurrency: ${ex.message}", ex)
                }
            }
        }
        if (currencyHistories.isNotEmpty()) {
            currencyHistoryRepository.saveAll(currencyHistories)
        }
    }


    private fun initCurrencyHistories(yahooResponse: CurrencyHistoryList): List<CurrencyHistory> {
        val fromCurrency = currencyRepository.getReferenceById(yahooResponse.fromCurrency)
        val toCurrency = currencyRepository.getReferenceById(yahooResponse.toCurrency)
        val result = mutableListOf<CurrencyHistory>()
        yahooResponse.exchangeRates.forEach { (date, exchangeRate) ->
            result.add(CurrencyHistory(
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                validDate = date,
                exchangeRate = exchangeRate
            ))
        }

        return result
    }
}