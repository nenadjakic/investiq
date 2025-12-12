package com.github.nenadjakic.investiq.service.importer

import com.github.nenadjakic.investiq.common.enum.IBKRAction
import com.github.nenadjakic.investiq.common.dto.ImportError
import com.github.nenadjakic.investiq.common.dto.ImportResult
import com.github.nenadjakic.investiq.common.dto.ImportSummary
import com.github.nenadjakic.investiq.common.dto.RowResult
import com.github.nenadjakic.investiq.common.dto.RowStatus
import com.github.nenadjakic.investiq.common.dto.ValidationResult
import com.github.nenadjakic.investiq.service.model.importer.IBKRTrade
import com.github.nenadjakic.investiq.service.model.importer.toStagingTransactions
import com.github.nenadjakic.investiq.data.entity.asset.AssetAlias
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Tag
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.repository.AssetAliasRepository
import com.github.nenadjakic.investiq.data.repository.CurrencyRepository
import com.github.nenadjakic.investiq.data.repository.StagingTransactionRepository
import com.github.nenadjakic.investiq.data.repository.TagRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service("ibkrImportesService")
class IBKRImportesService(
    private val assetAliasRepository: AssetAliasRepository,
    private val currencyRepository: CurrencyRepository,
    private val stagingTransactionRepository: StagingTransactionRepository,
    private val tagRepository: TagRepository
): ImporterService<IBKRTrade> {
    companion object {
        val TRADES_HEADERS = listOf(
            "Trades", "Header", "DataDiscriminator", "Asset Category", "Currency", "Symbol",
            "Date/Time", "Quantity", "T. Price", "C. Price", "Proceeds", "Comm/Fee",
            "Basis", "Realized P/L", "MTM P/L", "Code"
        )

        val DIVIDENDS_HEADERS = listOf(
            "Dividends", "Header", "Currency", "Date", "Description", "Amount"
        )

        val WITHHOLDING_HEADERS = listOf(
            "Withholding Tax", "Header", "Currency", "Date", "Description", "Amount", "Code"
        )

        val DEPOSITS_HEADERS = listOf(
            "Deposits & Withdrawals", "Header", "Currency", "Settle Date", "Description", "Amount"
        )
    }

    override fun validateHeaders(input: InputStream): ValidationResult {
        TODO("Not yet implemented")
    }

    override fun import(input: InputStream): ImportResult<IBKRTrade> {
        val format = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(false)
            .setTrim(true)
            .setIgnoreSurroundingSpaces(true)
            .setQuote('"')
            .get()

        val deposits = mutableListOf<CSVRecord>()
        val trades = mutableListOf<CSVRecord>()
        val dividends = mutableListOf<CSVRecord>()
        val withholdings = mutableListOf<CSVRecord>()

        CSVParser.parse(input, StandardCharsets.UTF_8, format).use { parser ->
            var currentSection: String? = null

            for ((_, record) in parser.records.withIndex()) {
                if (record.all { it.isBlank() }) {
                    continue
                }

                val newSection = when {
                    recordMatchesHeader(record, DEPOSITS_HEADERS) -> "Deposits"
                    recordMatchesHeader(record, TRADES_HEADERS) -> "Trades"
                    recordMatchesHeader(record, DIVIDENDS_HEADERS) -> "Dividends"
                    recordMatchesHeader(record, WITHHOLDING_HEADERS) -> "Withholding"
                    else -> null
                }

                if (record.any { it.trim() == "Header" }) {
                    currentSection = newSection
                    continue
                }

                if (currentSection == null) {
                    continue
                }

                val headerCol = record.get(1)?.trim() ?: continue
                if (headerCol != "Data") {
                    continue
                }

                when (currentSection) {
                    "Trades" ->  trades.add(record)
                    "Dividends" -> {
                        if (record.get(2).contains("Total")) {
                            continue
                        }
                        dividends.add(record)
                    }
                    "Withholding" -> {
                        if (record.get(2).contains("Total")) {
                            continue
                        }
                        withholdings.add(record)
                    }
                    "Deposits" -> {
                        if (record.get(2).equals("Total")) {
                            continue
                        }
                        deposits.add(record)
                    }
                }
            }

        }

        val errors = mutableListOf<ImportError>()
        val rowResults = mutableListOf<RowResult<IBKRTrade>>()

        var total = 0
        var success = 0
        var failed = 0
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss")

        deposits.forEachIndexed { index, deposit ->
            total++

            try {
                val amount = deposit.get(5)!!.toDouble()
                val time = run {
                    val strTime = deposit.get(3)!!
                    LocalDate.parse(strTime).atTime(0, 0)
                }
                val action = if (amount > 0) IBKRAction.DEPOSIT else IBKRAction.WITHDRAWAL
                val trade = IBKRTrade(
                    action = action,
                    time = time,
                    amount = amount,
                    currency = deposit.get(2)
                )
                rowResults.add(RowResult(index, RowStatus.SUCCESS, trade))
                success++
            } catch (e: Exception) {
                rowResults.add(RowResult(index, RowStatus.FAILED))
                errors.add(ImportError(index, message = e.message ?: "Unknown error"))
                failed++
            }
        }
        trades.forEachIndexed { index, trade ->
            total++

            try {
                val quantity = trade.get(7)!!.toDouble()
                val time = run {
                    val strTime = trade.get(6)!!
                    LocalDateTime.parse(strTime, formatter)
                }
                val action = if (quantity > 0) IBKRAction.BUY else IBKRAction.SELL
                val trade = IBKRTrade(
                    action = action,
                    time = time,
                    ticker = trade.get(5)!!,
                    quantity = quantity,
                    price = trade.get(8).toDouble(),
                    commission = trade.get(11).toDouble(),
                    currency = trade.get(4)
                )
                rowResults.add(RowResult(index, RowStatus.SUCCESS, trade))
                success++
            } catch (e: Exception) {
                rowResults.add(RowResult(index, RowStatus.FAILED))
                errors.add(ImportError(index, message = e.message ?: "Unknown error"))
                failed++
            }
        }


        dividends.forEachIndexed { index, dividend ->
            total++

            fun extractTicker(value: String): String {
                return value.substringBefore("(")
            }

            try {
                val time = run {
                    val strTime = dividend.get(3)!!
                    LocalDate.parse(strTime).atTime(0, 0)
                }
                val ticker = run {
                    val description = dividend.get(4)

                    extractTicker(description)
                }
                val trade = IBKRTrade(
                    action = IBKRAction.DIVIDEND,
                    time = time,
                    ticker = ticker,
                    amount = dividend.get(5).toDouble(),
                    taxAmount = 0.0,
                    currency = dividend.get(2)
                ).also { trade ->

                    withholdings
                        .firstOrNull {
                            LocalDate.parse(it.get(3)).atTime(0, 0).equals(trade.time)
                                    && extractTicker(it.get(4)) == trade.ticker
                        }
                        ?.let {
                            trade.taxAmount = it.get(5).toDouble()
                        }
                }
                rowResults.add(RowResult(index, RowStatus.SUCCESS, trade))
                success++
            } catch (e: Exception) {
                rowResults.add(RowResult(index, RowStatus.FAILED))
                errors.add(ImportError(index, message = e.message ?: "Unknown error"))
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

    private fun recordMatchesHeader(record: CSVRecord, header: List<String>): Boolean {
        return header.withIndex().all { (idx, name) ->
            record.size() > idx && record.get(idx).trim() == name
        }
    }


    private fun addToStaging(rowResults: MutableList<RowResult<IBKRTrade>>) {
        val assetAliases = mutableListOf<AssetAlias>()
        val currencies = mutableMapOf<String, Currency>()
        val tags = mutableMapOf<String, Tag>()
        try {
            assetAliases.addAll(
                assetAliasRepository.findAllByPlatform(Platform.IBKR)
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