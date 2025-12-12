package com.github.nenadjakic.investiq.service.importer

import com.github.nenadjakic.investiq.data.entity.asset.AssetAlias
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Tag
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.repository.AssetAliasRepository
import com.github.nenadjakic.investiq.data.repository.CurrencyRepository
import com.github.nenadjakic.investiq.data.repository.StagingTransactionRepository
import com.github.nenadjakic.investiq.data.repository.TagRepository
import com.github.nenadjakic.investiq.common.enum.RevolutAction
import com.github.nenadjakic.investiq.common.dto.ImportError
import com.github.nenadjakic.investiq.common.dto.ImportResult
import com.github.nenadjakic.investiq.common.dto.ImportSummary
import com.github.nenadjakic.investiq.common.dto.RowResult
import com.github.nenadjakic.investiq.common.dto.RowStatus
import com.github.nenadjakic.investiq.common.dto.ValidationResult
import com.github.nenadjakic.investiq.service.model.importer.RevolutTrade
import com.github.nenadjakic.investiq.service.model.importer.toStagingTransactions
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service("revolutImporterService")
class RevolutImporterService(
    private val assetAliasRepository: AssetAliasRepository,
    private val currencyRepository: CurrencyRepository,
    private val stagingTransactionRepository: StagingTransactionRepository,
    private val tagRepository: TagRepository
): ImporterService<RevolutTrade> {

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
            fun get(name: String): String =
                record.get(name) ?: throw IllegalArgumentException("Missing required field: $name")

            fun getLocalDateTime(name: String): LocalDateTime {
                val value = get(name).trim()
                if (value.isEmpty()) throw IllegalArgumentException("Missing required date field: $name")

                val formatters = listOf(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                )

                for (formatter in formatters) {
                    try {
                        return LocalDateTime.parse(value, formatter)
                    } catch (_: Exception) { }
                }

                throw IllegalArgumentException("Invalid date format for field: $name → $value")
            }


            fun getDouble(name: String): Double {
                val value = get(name).trim()
                if (value.isEmpty()) return 0.0
                val normalized = value.replace(",", ".")
                return normalized.toDoubleOrNull()
                    ?: throw IllegalArgumentException("Invalid number in field: $name, value: '$value'")
            }

            fun getDoubleOrDefault(name: String, default: Double = 0.0): Double {
                val value = try { record.get(name)?.trim() } catch (_: Exception) { null }
                if (value.isNullOrEmpty()) return default
                val normalized = value.replace(",", ".")
                return normalized.toDoubleOrNull() ?: default
            }

            fun getOrDefault(name: String, default: String? = null): String? =
                try { record.get(name)?.takeIf { it.isNotBlank() } } catch (_: Exception) { null }
                    ?: default

            val price = run {
                val strPrice = record["Price per share"]
                if (strPrice.isNotEmpty()) {
                    extractNumber(strPrice)
                } else {
                    null
                }
            }
            val totalAmount = run {
                val strTotalAmount = record["Total Amount"]
                if (strTotalAmount.isNotEmpty()) {
                    extractNumber(strTotalAmount)
                } else {
                    0.0
                }
            }
            return RevolutTrade(
                action = RevolutAction.fromValue(get("Type")),
                time = getLocalDateTime("Date"),
                ticker = get("Ticker"),
                quantity = getDouble("Quantity"),
                price = price,
                totalAmount = totalAmount,
                currency = get("Currency"),
                fxRate = getDouble("FX Rate")
            ).also {
                if (it.action == RevolutAction.MARKET_BUY || it.action == RevolutAction.MARKET_SELL) {
                    if (it.quantity!! * it.price!! < it.totalAmount) {
                        it.commissionAmount = it.totalAmount - (it.quantity * it.price)
                    }
                }
            }
        }

        private fun addToStaging(rowResults: MutableList<RowResult<RevolutTrade>>) {
            val assetAliases = mutableListOf<AssetAlias>()
            val currencies = mutableMapOf<String, Currency>()
            val tags = mutableMapOf<String, Tag>()
            try {
                assetAliases.addAll(
                    assetAliasRepository.findAllByPlatform(Platform.REVOLUT)
                )

                currencies.putAll(
                    currencyRepository.findAll().associateBy { it.code!! })

                tags.putAll(
                    tagRepository.findAll().associateBy { it.name }
                )

            } catch (ex: Exception) {
                throw RuntimeException("Failed to load account for importing Trading 212 transactions.", ex)
            }

            val stagingTransactions = mutableListOf<StagingTransaction>()

            for (rowResult in rowResults) {
                if (rowResult.status == RowStatus.SUCCESS) {
                    stagingTransactions.addAll(
                        rowResult.mappedObjectInfo!!.toStagingTransactions(assetAliases, currencies, tags))
                }
            }
            stagingTransactionRepository.saveAll(stagingTransactions)
        }

        fun extractNumber(input: String): Double {
            return input.replace("[^0-9.]".toRegex(), "").toDouble()
        }
    }