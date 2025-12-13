package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.IndustryResponse
import com.github.nenadjakic.investiq.common.dto.SectorSimpleResponse
import com.github.nenadjakic.investiq.data.repository.IndustryRepository
import org.springframework.stereotype.Service

@Service
class IndustryService(
    private val industryRepository: IndustryRepository
) {
    fun findAll(): List<IndustryResponse> =
        industryRepository.findAll().map {
            IndustryResponse(
                it.id!!,
                it.name,
                SectorSimpleResponse(
                    it.sector.id!!,
                    it.sector.name
                )
            )
        }
}