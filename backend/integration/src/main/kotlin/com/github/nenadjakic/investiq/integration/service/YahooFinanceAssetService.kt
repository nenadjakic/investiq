package com.github.nenadjakic.investiq.integration.service

import com.github.nenadjakic.investiq.integration.dto.AssetHistoryList
import com.github.nenadjakic.investiq.integration.dto.DatePrices
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Service
class YahooFinanceAssetService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
): AbstractYahooFinanceService(
    restTemplate,
    objectMapper
)  {

    fun fetchHistory(
        symbol: String,
        fromDate: LocalDate,
        toDate: LocalDate
    ): AssetHistoryList {
        val yahooChartResponse = fetchChart(symbol, fromDate, toDate)
            ?: throw IllegalStateException("No data found for $symbol")

        val result = yahooChartResponse.chart?.result?.firstOrNull() ?: throw IllegalStateException("No data found")
        val returnResponse = AssetHistoryList(symbol)

        val timestamps = result.timestamp.orEmpty()
        val quote = result.indicators?.quote?.firstOrNull()
        val closes = quote?.close.orEmpty()
        val opens = quote?.open.orEmpty()
        val highs = quote?.high.orEmpty()
        val lows = quote?.low.orEmpty()
        val volumes = quote?.volume.orEmpty()

        val adjCloseList = result.indicators?.adjclose?.firstOrNull()?.adjclose

        timestamps.forEachIndexed { idx, ts ->
            val closeNullable = closes.getOrNull(idx)
            val adjCloseNullable = adjCloseList?.getOrNull(idx)
            val openNullable = opens.getOrNull(idx)
            val highNullable = highs.getOrNull(idx)
            val lowNullable = lows.getOrNull(idx)
            val volumeNullable = volumes.getOrNull(idx)

            if (closeNullable == null && adjCloseNullable == null) return@forEachIndexed

            val dt = LocalDate.ofInstant(Instant.ofEpochSecond(ts), ZoneId.of("UTC"))
            if (dt.isBefore(fromDate) || dt.isAfter(toDate)) return@forEachIndexed

            fun toBigDecimalOrNull(v: Double?): BigDecimal? = v?.let { BigDecimal.valueOf(it).setScale(6, RoundingMode.HALF_UP) }

            returnResponse.prices.add(
                DatePrices(
                    date = dt,
                    volume = volumeNullable,
                    open = toBigDecimalOrNull(openNullable),
                    highPrice = toBigDecimalOrNull(highNullable),
                    lowPrice = toBigDecimalOrNull(lowNullable),
                    closePrice = toBigDecimalOrNull(closeNullable),
                    adjustedClose = toBigDecimalOrNull(adjCloseNullable)
                )
            )
        }


        return returnResponse
    }
}