package com.github.nenadjakic.investiq.importer.model

import com.github.nenadjakic.investiq.importer.enum.EToroAction
import jdk.jfr.DataAmount
import java.time.LocalDateTime

data class RevolutTrade (
    val action: EToroAction,
    val time: LocalDateTime,
    val ticker: String?,
    val quantity: Double,
    val price: Double,
    val total: Double,
    val amount: Double,
    val currency: String,
    var fxRate: Double,
)