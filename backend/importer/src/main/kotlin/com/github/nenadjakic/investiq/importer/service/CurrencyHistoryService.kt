package com.github.nenadjakic.investiq.importer.service

import com.github.nenadjakic.investiq.currencyfetcher.service.FrankfurterService
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.repository.CurrencyHistoryRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class CurrencyHistoryService(
    private val currencyHistoryRepository: CurrencyHistoryRepository,
    private val frankfurterService: FrankfurterService
) {

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