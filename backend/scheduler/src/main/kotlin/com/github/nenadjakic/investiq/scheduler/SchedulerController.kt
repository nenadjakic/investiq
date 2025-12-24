package com.github.nenadjakic.investiq.scheduler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/scheduler")
class SchedulerController(
    private val assetScheduler: AssetScheduler,
    private val currencyScheduler: CurrencyScheduler,
    private val snapshotScheduler: SnapshotScheduler
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/asset")
    fun triggerAsset(): ResponseEntity<String> {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                assetScheduler.fetchAssetHistories()
            } catch (ex: Exception) {
                log.error("Error while running asset scheduler", ex)
            }
        }
        return ResponseEntity.accepted().body("Asset scheduler triggered")
    }

    @PostMapping("/currency")
    fun triggerCurrency(): ResponseEntity<String> {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                currencyScheduler.fetchCurrencyHistories()
            } catch (ex: Exception) {
                log.error("Error while running currency scheduler", ex)
            }
        }
        return ResponseEntity.accepted().body("Currency scheduler triggered")
    }

    @PostMapping("/snapshot")
    fun triggerSnapshot(): ResponseEntity<String> {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                snapshotScheduler.generateSnapshots()
            } catch (ex: Exception) {
                log.error("Error while running snapshot scheduler", ex)
            }
        }
        return ResponseEntity.accepted().body("Snapshot scheduler triggered")
    }

    @PostMapping("/all")
    fun triggerAll(): ResponseEntity<String> {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                assetScheduler.fetchAssetHistories()
                currencyScheduler.fetchCurrencyHistories()
                snapshotScheduler.generateSnapshots()
            } catch (ex: Exception) {
                log.error("Error while running all schedulers", ex)
            }
        }
        return ResponseEntity.accepted().body("All schedulers triggered")
    }

    @PostMapping("/snapshot/range")
    fun triggerSnapshotRange(
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate
    ): ResponseEntity<String> {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                snapshotScheduler.generateSnapshots(from, to)
            } catch (ex: Exception) {
                log.error("Error while running snapshot scheduler for range", ex)
            }
        }
        return ResponseEntity.accepted().body("Snapshot scheduler triggered for range $from to $to")
    }
}
