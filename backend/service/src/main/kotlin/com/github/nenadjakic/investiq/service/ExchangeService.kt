package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.common.dto.exchange.ExchangeAddRequest
import com.github.nenadjakic.investiq.common.dto.exchange.ExchangeResponse
import com.github.nenadjakic.investiq.common.extension.toExchangeResponse
import com.github.nenadjakic.investiq.data.entity.core.Exchange
import com.github.nenadjakic.investiq.data.repository.CountryRepository
import com.github.nenadjakic.investiq.data.repository.ExchangeRepository
import org.springframework.stereotype.Service

@Service
class ExchangeService(
    private val exchangeRepository: ExchangeRepository,
    private val countryRepository: CountryRepository,
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

    fun create(request: ExchangeAddRequest): ExchangeResponse {
        return exchangeRepository.save(
            Exchange(
                name = request.name!!,
                mic = request.mic!!,
                acronym = request.acronym!!,
                country = countryRepository.getReferenceById(request.countryId!!)
            )
        ).toExchangeResponse()
    }
}