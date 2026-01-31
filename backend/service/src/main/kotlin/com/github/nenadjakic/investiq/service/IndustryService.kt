package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.industry.IndustryResponse
import com.github.nenadjakic.investiq.common.dto.SectorSimpleResponse
import com.github.nenadjakic.investiq.common.dto.industry.IndustryAddRequest
import com.github.nenadjakic.investiq.common.dto.industry.IndustrySimpleResponse
import com.github.nenadjakic.investiq.common.extension.toIndustryResponse
import com.github.nenadjakic.investiq.data.entity.core.Industry
import com.github.nenadjakic.investiq.data.repository.IndustryRepository
import com.github.nenadjakic.investiq.data.repository.SectorRepository
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID
import kotlin.jvm.optionals.getOrNull
import kotlin.uuid.Uuid

@Service
class IndustryService(
    private val industryRepository: IndustryRepository,
    private val sectorRepository: SectorRepository,
) {
    fun findAll(): List<IndustryResponse> =
        industryRepository.findAll().map { it.toIndustryResponse()  }

    fun findById(id: UUID): Optional<IndustryResponse> =
        industryRepository.findById(id).map { it.toIndustryResponse() }

    fun create(request: IndustryAddRequest): IndustrySimpleResponse {
        return industryRepository.save(
            Industry(
                name = request.name!!,
                sector = sectorRepository.getReferenceById(request.sectorId!!),
            )
        ).let {
            IndustrySimpleResponse(
                it.id!!,
                it.name
            )
        }
    }
}