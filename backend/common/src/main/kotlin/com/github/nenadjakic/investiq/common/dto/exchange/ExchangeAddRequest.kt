package com.github.nenadjakic.investiq.common.dto.exchange

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID
import kotlin.uuid.Uuid

data class ExchangeAddRequest (
    @field:NotBlank(message = "Exchange name is required")
    @field:Size(min = 2, max = 100, message = "Exchange name must be between 2 and 100 characters")
    val name: String?,

    @field:NotBlank(message = "MIC is required")
    @field:Size(max = 10, message = "MIC must be at most 10 characters")
    val mic: String?,

    @field:NotBlank(message = "Acronym is required")
    @field:Size(max = 10, message = "Acronym must be at most 10 characters")
    val acronym: String?,

    @field:NotBlank(message = "Country is required")
    val countryId: String?

)