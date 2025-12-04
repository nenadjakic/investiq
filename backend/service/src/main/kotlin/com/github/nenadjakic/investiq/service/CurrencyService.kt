package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.CurrencyResponse
import com.github.nenadjakic.investiq.common.dto.toResponse
import com.github.nenadjakic.investiq.data.repository.CurrencyRepository
import org.springframework.stereotype.Service

@Service
class CurrencyService(
    private val currencyRepository: CurrencyRepository
) {
    fun findAll(): List<CurrencyResponse> {
        return currencyRepository
            .findAll()
            .map { it.toResponse() }
    }
}