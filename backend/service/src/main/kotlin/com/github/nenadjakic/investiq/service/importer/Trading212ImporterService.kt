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
import com.github.nenadjakic.investiq.common.enum.Trading212Action
import com.github.nenadjakic.investiq.common.dto.ImportError
import com.github.nenadjakic.investiq.common.dto.ImportResult
import com.github.nenadjakic.investiq.common.dto.ImportSummary
import com.github.nenadjakic.investiq.common.dto.RowResult
import com.github.nenadjakic.investiq.common.dto.RowStatus
import com.github.nenadjakic.investiq.common.dto.ValidationResult
import com.github.nenadjakic.investiq.service.model.importer.Trading212Trade
import com.github.nenadjakic.investiq.service.model.importer.toStagingTransactions
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.text.isNotBlank
import kotlin.text.trim

@Service("trading212ImporterService")
class Trading212ImporterService(
    private val assetAliasRepository: AssetAliasRepository,
    private val currencyRepository: CurrencyRepository,
    private val stagingTransactionRepository: StagingTransactionRepository,
    private val tagRepository: TagRepository
): ImporterService<Trading212Trade> {
    companion object {
        val REQUIRED_HEADERS = listOf(
            "Action",
            "Time",
            "ISIN",
            "Ticker",
            "Name",
            "Notes",
            "ID",
            "No. of shares",
            "Price / share",
            "Currency (Price / share)",
            "Exchange rate",
            "Currency (Result)",
            "Total",
            "Currency (Total)",
            "Withholding tax",
            "Currency (Withholding tax)",
            //"Stamp duty reserve tax",
            //"Currency (Stamp duty reserve tax)",
            //"Currency conversion fee",
            //"Currency (Currency conversion fee)",
            //"French transaction tax",
            //"Currency (French transaction tax)"
        )
    }

    override fun validateHeaders(input: InputStream): ValidationResult {
        TODO("Not yet implemented")
    }

    override fun import(input: InputStream): ImportResult<Trading212Trade> {
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
            val rowResults = mutableListOf<RowResult<Trading212Trade>>()

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

    private fun mapRecord(record: CSVRecord): Trading212Trade {

        fun get(name: String): String =
            record.get(name) ?: throw IllegalArgumentException("Missing required field: $name")

        fun getLocalDateTime(name: String): LocalDateTime {
            val value = get(name).trim()
            if (value.isEmpty()) throw IllegalArgumentException("Missing required date field: $name")

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            return LocalDateTime.parse(value, formatter)
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

        return Trading212Trade(
            action = Trading212Action.fromValue(get("Action")),
            time = getLocalDateTime("Time"),
            isin = get("ISIN"),
            ticker = get("Ticker"),
            name = get("Name"),
            notes = record.get("Notes")?.takeIf { it.isNotBlank() },
            id = get("ID"),
            numberOfShares = getDouble("No. of shares"),
            pricePerShare = getDouble("Price / share"),
            currencyPricePerShare = get("Currency (Price / share)"),
            exchangeRate = getDouble("Exchange rate"),
            currencyResult = get("Currency (Result)"),
            total = getDouble("Total"),
            currencyTotal = get("Currency (Total)"),
            withholdingTax = getDoubleOrDefault("Withholding tax"),
            withholdingCurrency = getOrDefault("Currency (Withholding tax)"),
            stampDuty = getDoubleOrDefault("Stamp duty reserve tax"),
            stampCurrency = getOrDefault("Currency (Stamp duty reserve tax)"),
            fxFee = getDoubleOrDefault("Currency conversion fee"),
            fxFeeCurrency = getOrDefault("Currency (Currency conversion fee)"),
            frTax = getDoubleOrDefault("French transaction tax"),
            frTaxCurrency = getOrDefault("Currency (French transaction tax)"),
        )
    }

    private fun addToStaging(rowResults: MutableList<RowResult<Trading212Trade>>) {
        val assetAliases = mutableListOf<AssetAlias>()
        val currencies = mutableMapOf<String, Currency>()
        val tags = mutableMapOf<String, Tag>()
        try {
            assetAliases.addAll(
                assetAliasRepository.findAllByPlatform(Platform.TRADING212)
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
}