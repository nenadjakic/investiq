package com.github.nenadjakic.investiq.common.extension

import com.github.nenadjakic.investiq.common.dto.SectorSimpleResponse
import com.github.nenadjakic.investiq.common.dto.industry.IndustryResponse
import com.github.nenadjakic.investiq.data.entity.core.Industry

fun Industry.toIndustryResponse(): IndustryResponse =
    IndustryResponse(
        id = this.id!!,
        name = this.name,
        sector = SectorSimpleResponse(
            id = this.sector.id!!,
            name = this.sector.name
        )
)