package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.data.repository.CountryRepository
import org.springframework.stereotype.Service

@Service
class CountryService(
    private val countryRepository: CountryRepository
) {
    fun findAll(): List<CountryResponse> =
        countryRepository.findAll().map { country ->
            CountryResponse(country.iso2Code!!, country.name)
        }
}