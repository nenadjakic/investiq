package com.github.nenadjakic.investiq.currencyfetcher.service

import com.github.nenadjakic.investiq.currencyfetcher.model.FrankfurterResponse
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.LocalDate

@Service
class FrankfurterService(
    private val restTemplate: RestTemplate = RestTemplate()
) {
    private val baseUrl = "https://api.frankfurter.dev/v1"

    fun convert(fromCode: String, toCode: String, date: LocalDate? = null): BigDecimal? {
        try {
            val endpoint = if (date == null || date.isEqual(LocalDate.now())) "latest" else date.toString()

            val url = "$baseUrl/$endpoint?base=$fromCode&symbols=$toCode"

            val response: FrankfurterResponse? = restTemplate.getForObject(url, FrankfurterResponse::class.java)
            return response?.rates?.get(toCode)
        } catch (ex: Exception) {
            println("Frankfurter API error: ${ex.message}")
            return null
        }
    }
}