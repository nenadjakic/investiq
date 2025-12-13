package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.CompanyResponse
import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.common.dto.IndustryResponse
import com.github.nenadjakic.investiq.common.dto.SectorSimpleResponse
import com.github.nenadjakic.investiq.data.repository.CompanyRepository
import org.springframework.stereotype.Service

@Service
class CompanyService(
    private val companyRepository: CompanyRepository
) {
    fun findAll(): List<CompanyResponse> =
        companyRepository.findAll().map {
            CompanyResponse(
                it.id!!,
                it.name,
                CountryResponse(it.country.iso2Code!!, it.country.name),
                IndustryResponse(
                    it.industry.id!!,
                    it.industry.name,
                    it.industry.sector.let { sector ->
                        SectorSimpleResponse(
                            sector.id!!,
                            sector.name
                        )
                    }
                )
            )
        }
}