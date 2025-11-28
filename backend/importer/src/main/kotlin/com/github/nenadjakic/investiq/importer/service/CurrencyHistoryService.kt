package com.github.nenadjakic.investiq.importer.service

import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.repository.CurrencyHistoryRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class CurrencyHistoryService(
    private val currencyHistoryRepository: CurrencyHistoryRepository
) {

    fun convert(amount: BigDecimal, fromCurrency: Currency, toCurrency: Currency, date: LocalDate): BigDecimal {
        if (fromCurrency == toCurrency) return amount

        val direct = currencyHistoryRepository
            .findTopByFromCurrencyAndToCurrencyAndValidDateLessThanEqualOrderByValidDateDesc(
                fromCurrency,
                toCurrency,
                date
            )
        if (direct != null) {
            return amount.multiply(direct.exchangeRate)
        }

        val reverse = currencyHistoryRepository
            .findTopByFromCurrencyAndToCurrencyAndValidDateLessThanEqualOrderByValidDateDesc(
                toCurrency,
                fromCurrency,
                date
            )
        if (reverse != null) {
            val invertedRate = BigDecimal.ONE.divide(reverse.exchangeRate, 10, RoundingMode.HALF_UP)
            return amount.multiply(invertedRate)
        }

        throw NoSuchElementException(
            "No exchange rate available for ${fromCurrency.code} -> ${toCurrency.code} on or before $date"
        )
    }
}