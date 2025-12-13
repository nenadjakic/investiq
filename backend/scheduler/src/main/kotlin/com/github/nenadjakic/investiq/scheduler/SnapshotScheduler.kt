package com.github.nenadjakic.investiq.scheduler

import jakarta.transaction.Transactional
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcCall
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SnapshotScheduler(
    private val snapshotWorker: SnapshotWorker,
    private val jdbcTemplate: JdbcTemplate
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private val parallelism = 2

    @Scheduled(cron = "0 0 0 ? * MON")
    fun scheduleSnapshotGeneration() {
        runBlocking {
            generateSnapshots()
        }
    }

    suspend fun generateSnapshots() = coroutineScope {
        val existingDates =
            jdbcTemplate.queryForList(
                "SELECT DISTINCT snapshot_date FROM asset_daily_snapshots",
                LocalDate::class.java
            ).toSet()

        generateSequence(LocalDate.of(2024, 10, 1)) { it.plusDays(1) }
            .takeWhile { it < LocalDate.now() }
            .filter { it !in existingDates }
            .chunked(parallelism).forEach { chunk ->
                chunk.map { date ->
                    async { snapshotWorker.populateForDate(date) }
                }.awaitAll()
            }
    }
}

@Service
class SnapshotWorker(
    private val jdbcTemplate: JdbcTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    fun populateForDate(date: LocalDate) {
        log.info("Generating snapshot for date: $date")

        val call = SimpleJdbcCall(jdbcTemplate)
            .withFunctionName("populate_asset_daily_snapshots")

        call.execute(mapOf("p_snapshot_date" to java.sql.Date.valueOf(date)))
    }
}