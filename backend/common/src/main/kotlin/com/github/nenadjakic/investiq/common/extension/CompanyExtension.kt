package com.github.nenadjakic.investiq.common.extension

import com.github.nenadjakic.investiq.common.dto.CountryResponse
import com.github.nenadjakic.investiq.common.dto.industry.IndustryResponse
import com.github.nenadjakic.investiq.common.dto.SectorSimpleResponse
import com.github.nenadjakic.investiq.common.dto.company.CompanyResponse
import com.github.nenadjakic.investiq.data.entity.core.Company

fun Company.toCompanyResponse(): CompanyResponse {
    return CompanyResponse(
        this.id!!,
        this.name,
        CountryResponse(this.country.iso2Code!!, this.country.name),
        IndustryResponse(
            this.industry.id!!,
            this.industry.name,
            this.industry.sector.let { sector ->
                SectorSimpleResponse(
                    sector.id!!,
                    sector.name
                )
            }
        )
    )
}