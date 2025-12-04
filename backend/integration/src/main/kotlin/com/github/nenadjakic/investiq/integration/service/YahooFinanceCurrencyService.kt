package com.github.nenadjakic.investiq.integration.service

import com.github.nenadjakic.investiq.integration.dto.CurrencyHistoryList
import com.github.nenadjakic.investiq.integration.dto.DateExchangeRate
import com.github.nenadjakic.investiq.integration.dto.YahooChartResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@Service
class YahooFinanceCurrencyService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper = ObjectMapper(),
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val yahooBase = "https://query1.finance.yahoo.com/v8/finance/chart"

    fun  fetchHistory(
        fromCode: String,
        toCode: String,
        fromDate: LocalDate,
        toDate: LocalDate
    ): CurrencyHistoryList {
        require(!fromCode.isBlank() && !toCode.isBlank()) { "currency codes must be provided" }
        require(!toDate.isBefore(fromDate)) { "toDate must be >= fromDate" }

        val utcZone = ZoneOffset.UTC
        val periodFrom = fromDate.atStartOfDay(utcZone).toEpochSecond()
        val periodTo = toDate.plusDays(1).atStartOfDay(utcZone).toEpochSecond()

        val symbolCandidate = "${fromCode}${toCode}=X"
        val inverseCandidate = "${toCode}${fromCode}=X"

        var usedInverse = false
        var yahooChartResponse: YahooChartResponse?

        fun fetch(symbol: String): YahooChartResponse? {
            val url = "$yahooBase/$symbol?period1=$periodFrom&period2=$periodTo&interval=1d"
            log.info("Fetching Yahoo data: $url")
            val headers = HttpHeaders()
            headers.set(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"
            )
            val entity = HttpEntity<String>(headers)
            val raw = restTemplate.exchange(url, HttpMethod.GET, entity, String::class.java).body ?: return null
            return try {
                objectMapper.readValue(raw, YahooChartResponse::class.java)
            } catch (ex: Exception) {
                log.error("Failed to parse Yahoo JSON for $symbol: ${ex.message}")
                null
            }
        }

        yahooChartResponse = fetch(symbolCandidate)
        if (yahooChartResponse == null || yahooChartResponse.chart?.result.isNullOrEmpty()) {
            log.info("No result for $symbolCandidate, trying inverse $inverseCandidate")
            yahooChartResponse = fetch(inverseCandidate)
            if (yahooChartResponse == null || yahooChartResponse.chart?.result.isNullOrEmpty()) {
                throw IllegalStateException("No data found for $symbolCandidate or $inverseCandidate")
            } else {
                usedInverse = true
            }
        }

        val result = yahooChartResponse.chart?.result?.firstOrNull() ?: throw IllegalStateException("No data found")
        val returnResponse = CurrencyHistoryList(fromCode, toCode)

        val timestamps = result.timestamp!!
        val quote = result.indicators!!.quote!!.firstOrNull()
        val closes = quote?.close ?: emptyList()


        timestamps.forEachIndexed { idx, ts ->
            val priceNullable = closes.getOrNull(idx)
            if (priceNullable == null) {
                return@forEachIndexed
            }

            val dt = LocalDate.ofInstant(Instant.ofEpochSecond(ts), ZoneId.of("UTC"))

            if (dt.isBefore(fromDate) || dt.isAfter(toDate)) return@forEachIndexed

            val rate = if (!usedInverse) {
                BigDecimal.valueOf(priceNullable)
            } else {
                // invert: 1 / price
                if (priceNullable == 0.0) {
                    log.warn("Price is zero at $dt for inverted pair; skipping")
                    return@forEachIndexed
                }
                BigDecimal.ONE.divide(BigDecimal.valueOf(priceNullable), 12, RoundingMode.HALF_UP)
            }

            returnResponse.exchangeRates.add(DateExchangeRate(dt, rate))
        }

        return returnResponse
    }
}