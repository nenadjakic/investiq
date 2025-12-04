package com.github.nenadjakic.investiq.scheduler

import com.github.nenadjakic.investiq.data.entity.history.AssetHistory
import com.github.nenadjakic.investiq.data.entity.history.CurrencyHistory
import com.github.nenadjakic.investiq.data.repository.AssetHistoryRepository
import com.github.nenadjakic.investiq.data.repository.AssetRepository
import com.github.nenadjakic.investiq.integration.dto.AssetHistoryList
import com.github.nenadjakic.investiq.integration.service.YahooFinanceAssetService
import com.github.nenadjakic.investiq.service.AssetHistoryService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AssetScheduler(
    private val assetRepository: AssetRepository,
    private val assetHistoryRepository: AssetHistoryRepository,
    private val yahooFinanceAssetService: YahooFinanceAssetService,
    private val assetHistoryService: AssetHistoryService
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Value("\${investiq.scheduler.asset.fetch-delay-ms}")
    private var fetchDelayMs: Long? = null

    @Scheduled(fixedDelayString = "PT168H")
    fun fetchAssetHistories() {
        val assetHistories = mutableListOf<AssetHistory>()
        assetRepository
            .findAll()
            .map { it.symbol }
            .forEach { symbol ->
                try {
                    val latestValidDate = assetHistoryService.getLatestValidDate(symbol)
                    val fromDate = latestValidDate ?: LocalDate.of(2024, 10, 1)
                    val toDate = LocalDate.now()

                    val response = yahooFinanceAssetService.fetchHistory(symbol, fromDate, toDate)

                    assetHistories.addAll(initAssetHistories(response))
                    Thread.sleep(fetchDelayMs!!)
                } catch (ex: Exception) {
                    log.error("Error fetching data for $symbol: ${ex.message}", ex)
                }
            }
    }

    private fun initAssetHistories(yahooResponse: AssetHistoryList): List<AssetHistory> {
        val asset = assetRepository.findBySymbol(yahooResponse.symbol)!!
        val result = mutableListOf<AssetHistory>()
        yahooResponse.prices.forEach { (date, volume, open, highPrice, lowPrice, closePrice, adjustedClose) ->
            result.add(AssetHistory(
                asset = asset,
                validDate = date,
                volume = volume,
                openPrice = open,
                highPrice = highPrice,
                lowPrice = lowPrice,
                closePrice = closePrice!!,
                adjustedClose = adjustedClose
            ))
        }
        return result
    }
}