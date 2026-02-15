package com.github.nenadjakic.investiq.common.dto.transaction

import com.github.nenadjakic.investiq.data.enum.Platform
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class WithdrawalRequest (
    val transactionId: UUID? = null,

    @param:NotNull(message = "Platform must not be null")
    val platform: Platform? = null,

    @param:NotNull(message = "Transaction date must not be null")
    val transactionDate: LocalDateTime? = null,

    @param:NotNull(message = "Amount must not be null")
    @param:DecimalMin(value = "0", inclusive = false, message = "Amount must be more than 0")
    val amount: BigDecimal? = null,

    @param:NotEmpty(message = "Currency must not be empty")
    val currency: String? = null,

    val fee: BigDecimal? = null
)

