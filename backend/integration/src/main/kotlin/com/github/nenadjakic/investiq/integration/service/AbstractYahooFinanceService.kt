package com.github.nenadjakic.investiq.integration.service

import com.github.nenadjakic.investiq.integration.dto.YahooChartResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import tools.jackson.databind.ObjectMapper
import java.time.LocalDate
import java.time.ZoneOffset

abstract class AbstractYahooFinanceService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {
    protected val log: Logger = LoggerFactory.getLogger(javaClass)
    protected val yahooBase = "https://query1.finance.yahoo.com/v8/finance/chart"
    protected val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"

    protected fun epochSecondsStartOfDayUtc(date: LocalDate): Long {
        val utcZone = ZoneOffset.UTC
        return date.atStartOfDay(utcZone).toEpochSecond()
    }

    protected fun fetchChart(symbol: String, fromDate: LocalDate, toDate: LocalDate): YahooChartResponse? {
        require(symbol.isNotBlank()) { "symbol must be provided" }
        require(!toDate.isBefore(fromDate)) { "toDate must be >= fromDate" }


        val periodFrom = epochSecondsStartOfDayUtc(fromDate)
        val periodTo = epochSecondsStartOfDayUtc(toDate.plusDays(1))


        val url = "$yahooBase/$symbol?period1=$periodFrom&period2=$periodTo&interval=1d"
        log.debug("Fetching Yahoo data: $url")


        val headers = HttpHeaders()
        headers.set("User-Agent", userAgent)
        val entity = HttpEntity<String>(headers)


        val raw = try {
            restTemplate.exchange(url, HttpMethod.GET, entity, String::class.java).body
        } catch (ex: Exception) {
            log.error("HTTP request failed for $symbol: ${ex.message}")
            null
        } ?: return null


        return try {
            objectMapper.readValue(raw, YahooChartResponse::class.java)
        } catch (ex: Exception) {
            log.error("Failed to parse Yahoo JSON for $symbol: ${ex.message}")
            null
        }
    }
}