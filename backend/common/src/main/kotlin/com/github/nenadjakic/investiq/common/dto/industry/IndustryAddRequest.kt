package com.github.nenadjakic.investiq.common.dto.industry

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class IndustryAddRequest(
    @field:NotNull(message = "Sector ID is required")
    val sectorId: UUID?,

    @field:NotBlank(message = "Industry name is required")
    @field:Size(min = 2, max = 100, message = "Industry name must be between 2 and 100 characters")
    val name: String?
)
