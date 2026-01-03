package com.github.nenadjakic.investiq.scheduler

import com.github.nenadjakic.investiq.scheduler.service.EmailService
import com.github.nenadjakic.investiq.scheduler.service.ReportService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ReportScheduler(
    private val reportService: ReportService,
    private val emailService: EmailService
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Weekly scheduler - triggers every Monday at 8:00 AM
     * Uses yesterday's date with period type "W"
     */
    @Scheduled(cron = "0 0 8 ? * MON")
    fun generateWeeklyReport() {
        log.info("Starting weekly portfolio report generation...")
        try {
            val reportDate = LocalDate.now().minusDays(1)
            val periodType = "W"

            val pdfPath = reportService.generatePortfolioPeriodReport(reportDate, periodType)
            emailService.sendPortfolioPeriodReport(pdfPath, reportDate, periodType)

            log.info("Weekly portfolio report generated and sent successfully")
        } catch (e: Exception) {
            log.error("Error generating weekly report", e)
        }
    }

    /**
     * Monthly scheduler - triggers on the 1st day of each month at 8:00 AM
     * Uses yesterday's date (last day of previous month) with period type "M"
     */
    @Scheduled(cron = "0 0 8 1 * ?")
    fun generateMonthlyReport() {
        log.info("Starting monthly portfolio report generation...")
        try {
            val reportDate = LocalDate.now().minusDays(1)
            val periodType = "M"

            val pdfPath = reportService.generatePortfolioPeriodReport(reportDate, periodType)
            emailService.sendPortfolioPeriodReport(pdfPath, reportDate, periodType)

            log.info("Monthly portfolio report generated and sent successfully")
        } catch (e: Exception) {
            log.error("Error generating monthly report", e)
        }
    }

    /**
     * Yearly scheduler - triggers on January 1st at 8:00 AM
     * Uses yesterday's date (December 31st of previous year) with period type "Y"
     */
    @Scheduled(cron = "0 0 8 1 1 ?")
    fun generateYearlyReport() {
        log.info("Starting yearly portfolio report generation...")
        try {
            val reportDate = LocalDate.now().minusDays(1)
            val periodType = "Y"

            val pdfPath = reportService.generatePortfolioPeriodReport(reportDate, periodType)
            emailService.sendPortfolioPeriodReport(pdfPath, reportDate, periodType)

            log.info("Yearly portfolio report generated and sent successfully")
        } catch (e: Exception) {
            log.error("Error generating yearly report", e)
        }
    }

    /**
     * Manual trigger for testing - can be called via controller or manually
     */
    fun generateReportManually(reportDate: LocalDate, periodType: String) {
        log.info("Manual report generation triggered for date: $reportDate, type: $periodType")
        try {
            val pdfPath = reportService.generatePortfolioPeriodReport(reportDate, periodType)
            emailService.sendPortfolioPeriodReport(pdfPath, reportDate, periodType)
            log.info("Manual report generated and sent successfully")
        } catch (e: Exception) {
            log.error("Error generating manual report", e)
            throw e
        }
    }
}

