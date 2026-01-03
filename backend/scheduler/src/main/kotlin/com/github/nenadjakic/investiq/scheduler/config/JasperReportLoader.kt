package com.github.nenadjakic.investiq.scheduler.config

import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.util.JRLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

/**
 * Loads pre-compiled Jasper report files (.jasper) from classpath at startup.
 * The .jasper files are pre-compiled from .jrxml files and stored in resources.
 */
@Component
class JasperReportLoader : CommandLineRunner {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    // Cache for loaded reports - avoids reloading from classpath on each request
    private val loadedReports = mutableMapOf<String, JasperReport>()

    override fun run(vararg args: String) {
        log.info("Loading pre-compiled Jasper reports from classpath...")

        try {
            // Load all .jasper files from resources
            loadedReports["portfolio_period_report"] = loadReport("portfolio_period_report.jasper")
            loadedReports["report_holdings"] = loadReport("report_holdings.jasper")
            loadedReports["portfolio_summary"] = loadReport("portfolio_summary.jasper")

            log.info("✓ All Jasper reports loaded successfully (${loadedReports.size} reports)")
        } catch (e: Exception) {
            log.error("✗ Error loading Jasper reports", e)
            throw e
        }
    }

    private fun loadReport(jasperFileName: String): JasperReport {
        log.info("Loading $jasperFileName...")
        val resource = ClassPathResource(jasperFileName)

        if (!resource.exists()) {
            throw IllegalStateException("✗ Compiled report not found: $jasperFileName. " +
                    "Make sure .jasper files are in src/main/resources/")
        }

        return resource.inputStream.use { stream ->
            val report = JRLoader.loadObject(stream) as JasperReport
            log.info("✓ Loaded: $jasperFileName")
            report
        }
    }

    /**
     * Get a loaded report by name
     * @param reportName The report name without extension (e.g., "portfolio_period_report")
     * @return The loaded JasperReport
     */
    fun getReport(reportName: String): JasperReport {
        return loadedReports[reportName]
            ?: throw IllegalStateException("Report not found: $reportName. " +
                    "Available reports: ${loadedReports.keys}")
    }
}

