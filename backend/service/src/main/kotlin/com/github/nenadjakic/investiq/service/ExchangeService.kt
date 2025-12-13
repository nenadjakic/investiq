package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.common.dto.ExchangeResponse
import com.github.nenadjakic.investiq.data.repository.ExchangeRepository
import org.springframework.stereotype.Service

@Service
class ExchangeService(
    private val exchangeRepository: ExchangeRepository
) {

    fun findAll(): List<ExchangeResponse> =
        exchangeRepository.findAll().map { exchange ->
            ExchangeResponse(
                exchange.id!!,
                exchange.mic,
                exchange.acronym,
                exchange.name,
                CountryResponse(exchange.country.iso2Code!!, exchange.country.name)
            )
        }
}