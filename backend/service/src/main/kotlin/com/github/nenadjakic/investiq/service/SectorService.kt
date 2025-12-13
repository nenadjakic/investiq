package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.CompanyResponse
import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.common.dto.IndustryResponse
import com.github.nenadjakic.investiq.common.dto.IndustrySimpleResponse
import com.github.nenadjakic.investiq.common.dto.SectorResponse
import com.github.nenadjakic.investiq.common.dto.SectorSimpleResponse
import com.github.nenadjakic.investiq.data.repository.SectorRepository
import org.springframework.stereotype.Service

@Service
class SectorService (
    private val sectorRepository: SectorRepository
) {
    fun findAll(): List<SectorResponse> =
        sectorRepository.findAll().map {
            SectorResponse(
                it.id!!,
                it.name,
                it.industries.map { industry ->
                    IndustrySimpleResponse(
                        industry.id!!,
                        industry.name,
                    )
                }
            )
        }
}