package com.github.nenadjakic.investiq.integration.dto

import java.math.BigDecimal
import java.time.LocalDate

class FrankfurterResponse {
    var amount: BigDecimal? = null
    lateinit var base: String
    lateinit var date: LocalDate
    lateinit var rates: Map<String, BigDecimal>
}