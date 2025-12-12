package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.history.CurrencyHistory
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface CurrencyHistoryRepository: JpaRepository<CurrencyHistory, UUID> {

    /**
     * Returns the most recent historical exchange rate for the given currency pair
     * where `validDate` is less than or equal to the specified date.
     *
     * This method is useful when performing historical conversions:
     * - First it searches for an exact match on the given date.
     * - If no record exists for that date, it returns the nearest earlier rate.
     *
     * @param fromCurrency The source currency (the currency being converted from).
     * @param toCurrency The target currency (the currency being converted to).
     * @param date The date for which the historical rate is requested.
     * @return The closest matching `CurrencyHistory` entry, or `null` if none exist.
     */
    fun findTopByFromCurrency_CodeAndToCurrency_CodeAndValidDateLessThanEqualOrderByValidDateDesc(
        fromCurrency: String,
        toCurrency: String,
        date: LocalDate
    ): CurrencyHistory?

    fun existsByFromCurrency_CodeAndToCurrency_CodeAndValidDate(
        fromCurrency: String,
        toCurrency: String,
        date: LocalDate
    ): Boolean

    fun findTopByFromCurrency_CodeAndToCurrency_CodeOrderByValidDateDesc(
        fromCurrency: String,
        toCurrency: String
    ): CurrencyHistory?
}