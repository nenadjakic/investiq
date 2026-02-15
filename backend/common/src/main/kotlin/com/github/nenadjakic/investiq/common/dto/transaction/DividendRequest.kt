package com.github.nenadjakic.investiq.common.dto.transaction

import com.github.nenadjakic.investiq.data.enum.Platform
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class DividendRequest (
    val transactionId: UUID? = null,

    @param:NotNull(message = "Platform must not be null")
    val platform: Platform? = null,

    @param:NotNull(message = "Transaction date must not be null")
    val transactionDate: LocalDateTime? = null,

    @param:NotNull(message = "Asset must not be null")
    val assetId: UUID? = null,

    @param:NotNull(message = "Gross amount must not be null")
    @param:DecimalMin(value = "0", inclusive = false, message = "Gross amount must be more than 0")
    val grossAmount: BigDecimal? = null,

    @param:DecimalMin(value = "0", inclusive = true, message = "Tax amount must be more than or equal to 0")
    val taxAmount: BigDecimal? = null,

    @param:DecimalMin(value = "0", inclusive = true, message = "Tax percentage must be more than or equal to 0")
    val taxPercentage: BigDecimal? = null,

    @param:NotEmpty(message = "Currency must not be empty")
    val currency: String? = null
) {
    fun getNetAmount(): BigDecimal? {
        return if (grossAmount != null && taxAmount != null) {
            grossAmount - taxAmount
        } else {
            null
        }
    }
}

