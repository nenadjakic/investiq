package com.github.nenadjakic.investiq.common.dto

import jakarta.validation.constraints.NotNull
import java.util.UUID

class StagingTransactionEditRequest {

    @NotNull
    var id: UUID? = null

    var quantity: Double? = null

    var price: Double? = null

    @NotNull
    var asset: UUID? = null

    var amount: Double? = null

    var grossAmount: Double? = null

    var taxPercentage: Double? = null

    var taxAmount: Double? = null
}