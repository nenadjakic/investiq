package com.github.nenadjakic.investiq.scheduler.service

import com.github.nenadjakic.investiq.scheduler.config.JasperReportLoader
import net.sf.jasperreports.engine.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.sql.DataSource

@Service
class ReportService(
    private val dataSource: DataSource,
    private val jasperReportLoader: JasperReportLoader
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Value($$"${investiq.scheduler.report.output-path}")
    private lateinit var outputPath: String


    /**
     * Generate a portfolio period report
     * @param reportDate The date for the report (usually yesterday)
     * @param periodType The type of period: W (weekly), M (monthly), Y (yearly)
     * @return The path to the generated PDF file
     */
    fun generatePortfolioPeriodReport(reportDate: LocalDate, periodType: String): String {
        log.info("Generating portfolio period report for date: $reportDate, type: $periodType")

        // Ensure output directory exists
        val outputDir = File(outputPath)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
            log.info("Created output directory: $outputPath")
        }

        // Generate filename with timestamp
        val dateStr = reportDate.format(DateTimeFormatter.ISO_DATE)
        val filename = "portfolio_period_report_${periodType}_${dateStr}.pdf"
        val outputFile = Paths.get(outputPath, filename).toFile()

        return try {
            dataSource.connection.use { connection ->
                // Get the main compiled report
                // Subreports (report_holdings.jasper and portfolio_summary.jasper)
                // will be automatically loaded by JasperReports from classpath
                val jasperReport = jasperReportLoader.getReport("portfolio_period_report")

                // Set report parameters
                val parameters = mapOf(
                    "REPORT_DATE" to java.sql.Date.valueOf(reportDate),
                    "PERIOD_TYPE" to periodType
                )

                // Fill the report with data
                log.info("Filling report with data from database...")
                val jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection)

                // Export to PDF
                log.info("Exporting report to PDF: ${outputFile.absolutePath}")
                JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile.absolutePath)

                log.info("✓ Report generated successfully: ${outputFile.absolutePath}")
                outputFile.absolutePath
            }
        } catch (e: Exception) {
            log.error("✗ Error generating report for date $reportDate and type $periodType", e)
            throw RuntimeException("Failed to generate report: ${e.message}", e)
        }
    }
}