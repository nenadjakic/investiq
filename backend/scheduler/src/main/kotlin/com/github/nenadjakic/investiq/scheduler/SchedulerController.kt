package com.github.nenadjakic.investiq.scheduler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
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
    private val snapshotScheduler: SnapshotScheduler,
    private val reportScheduler: ReportScheduler
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

    @PostMapping("/report")
    fun generateReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?,
        @RequestParam(defaultValue = "W") type: String
    ): ResponseEntity<Map<String, String>> {

        val reportDate = date ?: LocalDate.now().minusDays(1)
        val periodType = type.uppercase()

        if (periodType !in listOf("W", "M", "Y")) {
            return ResponseEntity.badRequest()
                .body(mapOf("error" to "Invalid period type. Must be W, M, or Y"))
        }

        return try {
            reportScheduler.generateReportManually(reportDate, periodType)
            ResponseEntity.ok(
                mapOf(
                    "status" to "success",
                    "message" to "Report generated and sent successfully",
                    "date" to reportDate.toString(),
                    "type" to periodType
                )
            )
        } catch (e: Exception) {
            ResponseEntity.internalServerError()
                .body(
                    mapOf(
                        "status" to "error",
                        "message" to "Failed to generate report: ${e.message}"
                    )
                )
        }
    }
}
