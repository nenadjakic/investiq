package com.github.nenadjakic.investiq.commonservice.service

import com.github.nenadjakic.investiq.commonservice.model.CurrencyHistoryRequest
import com.github.nenadjakic.investiq.currencyfetcher.service.FrankfurterService
import com.github.nenadjakic.investiq.currencyfetcher.service.YahooFinanceCurrencyFetcher
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.history.CurrencyHistory
import com.github.nenadjakic.investiq.data.repository.CurrencyHistoryRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.UUID

@Service
class CurrencyHistoryService(
    private val currencyHistoryRepository: CurrencyHistoryRepository,
    private val frankfurterService: FrankfurterService,
    private val yahooFinanceCurrencyFetcher: YahooFinanceCurrencyFetcher
) {

    fun add(currencyHistoryRequest: CurrencyHistoryRequest): UUID? {
        if (!currencyHistoryRepository.existsByFromCurrency_CodeAndToCurrency_CodeAndValidDate(
                currencyHistoryRequest.fromCurrency,
                currencyHistoryRequest.toCurrency,
                currencyHistoryRequest.date
            )
        ) {
            return currencyHistoryRepository.save(
                CurrencyHistory(
                    id = null,
                    fromCurrency = Currency().also { it.code = currencyHistoryRequest.fromCurrency },
                    toCurrency = Currency().also { it.code = currencyHistoryRequest.toCurrency },
                    validDate = currencyHistoryRequest.date,
                    exchangeRate = currencyHistoryRequest.exchangeRate
                )
            ).id
        }

        return null
    }

    fun convert(amount: BigDecimal, fromCurrency: String, toCurrency: String, date: LocalDate): BigDecimal {
        if (fromCurrency == toCurrency) return amount

        val direct = currencyHistoryRepository
            .findTopByFromCurrency_CodeAndToCurrency_CodeAndValidDateLessThanEqualOrderByValidDateDesc(
                fromCurrency,
                toCurrency,
                date
            )
        if (direct != null) {
            return amount.multiply(direct.exchangeRate)
        }

        val reverse = currencyHistoryRepository
            .findTopByFromCurrency_CodeAndToCurrency_CodeAndValidDateLessThanEqualOrderByValidDateDesc(
                toCurrency,
                fromCurrency,
                date
            )
        if (reverse != null) {
            val invertedRate = BigDecimal.ONE.divide(reverse.exchangeRate, 10, RoundingMode.HALF_UP)
            return amount.multiply(invertedRate)
        }

        frankfurterService
            .convert(fromCurrency, toCurrency, date)
            ?.let {
                return amount.multiply(it)
            }

        throw NoSuchElementException(
            "No exchange rate available for $fromCurrency -> $toCurrency on or before $date"
        )
    }
}