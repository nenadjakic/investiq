package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.company.CompanyResponse
import com.github.nenadjakic.investiq.common.dto.company.CompanyAddRequest
import com.github.nenadjakic.investiq.common.extension.toCompanyResponse
import com.github.nenadjakic.investiq.data.entity.core.Company
import com.github.nenadjakic.investiq.data.repository.CompanyRepository
import com.github.nenadjakic.investiq.data.repository.CountryRepository
import com.github.nenadjakic.investiq.data.repository.IndustryRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CompanyService(
    private val companyRepository: CompanyRepository,
    private val countryRepository: CountryRepository,
    private val industryRepository: IndustryRepository
) {
    @Transactional
    fun findAll(): List<CompanyResponse> =
        companyRepository.findAll().map { it.toCompanyResponse() }

    @Transactional
    fun create(request: CompanyAddRequest): CompanyResponse {
        Company(
            name = request.name!!,
            country = countryRepository.getReferenceById(request.countryCode!!),
            industry = industryRepository.getReferenceById(request.industryId!!),
        ).let {
            return companyRepository.save(it).toCompanyResponse()
        }
    }

    fun findById(id: UUID): CompanyResponse? =
        companyRepository.findByIdOrNull(id)?.toCompanyResponse()
}