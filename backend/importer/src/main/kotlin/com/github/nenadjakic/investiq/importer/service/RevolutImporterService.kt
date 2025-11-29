package com.github.nenadjakic.investiq.importer.service

import com.github.nenadjakic.investiq.importer.model.ImportError
import com.github.nenadjakic.investiq.importer.model.ImportResult
import com.github.nenadjakic.investiq.importer.model.ImportSummary
import com.github.nenadjakic.investiq.importer.model.RevolutTrade
import com.github.nenadjakic.investiq.importer.model.RowResult
import com.github.nenadjakic.investiq.importer.model.RowStatus
import com.github.nenadjakic.investiq.importer.model.ValidationResult
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.io.InputStream
import java.nio.charset.StandardCharsets

@Service("revolutImporterService")
class RevolutImporterService: ImporterService<RevolutTrade> {

    companion object {
        val REQUIRED_HEADERS = listOf(
            "Date",
            "Ticker",
            "Type",
            "Quantity",
            "Price per share",
            "Total Amount",
            "Currency",
            "FX Rate"
        )
    }

    override fun validateHeaders(input: InputStream): ValidationResult {
        TODO("Not yet implemented")
    }

    override fun import(input: InputStream): ImportResult<RevolutTrade> {
        val format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true)
            .setIgnoreSurroundingSpaces(true).get()

        CSVParser.parse(input, StandardCharsets.UTF_8, format).use { parser ->
            val headerKeys = parser.headerMap.keys.map { it.trim() }.toSet()
            val missing = REQUIRED_HEADERS.filterNot(headerKeys::contains)

            if (missing.isNotEmpty()) {
                return ImportResult(
                    summary = ImportSummary(0, 0, 0), errors = listOf(
                        ImportError(
                            rowIndex = null, message = "Missing headers: ${missing.joinToString()}"
                        )
                    )
                )
            }

            val errors = mutableListOf<ImportError>()
            val rowResults = mutableListOf<RowResult<RevolutTrade>>()

            var total = 0
            var success = 0
            var failed = 0

            for ((i, record) in parser.records.withIndex()) {
                val rowIndex = i + 1
                total++

                try {
                    val mapped = mapRecord(record)
                    rowResults.add(RowResult(rowIndex, RowStatus.SUCCESS, mapped))
                    success++
                } catch (e: Exception) {
                    rowResults.add(RowResult(rowIndex, RowStatus.FAILED))
                    errors.add(ImportError(rowIndex, message = e.message ?: "Unknown error"))
                    failed++
                }
            }

            addToStaging(rowResults)

            return ImportResult(
                summary = ImportSummary(totalRows = total, successfulRows = success, failedRows = failed),
                rowResults = rowResults,
                errors = errors
            )
        }
    }

        private fun mapRecord(record: CSVRecord): RevolutTrade {
            TODO()
        }

        private fun addToStaging(rowResults: MutableList<RowResult<RevolutTrade>>) {
            TODO()
        }
    }