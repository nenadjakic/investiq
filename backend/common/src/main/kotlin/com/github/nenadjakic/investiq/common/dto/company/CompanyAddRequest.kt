package com.github.nenadjakic.investiq.common.dto.company

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class CompanyAddRequest(
    @field:NotBlank(message = "Company name is required")
    @field:Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    val name: String?,

    @field:NotBlank(message = "Country code is required")
    @field:Size(min = 2, max = 2, message = "Country code must be 2 characters")
    val countryCode: String?,

    @field:NotNull(message = "Industry ID is required")
    val industryId: UUID?
)
