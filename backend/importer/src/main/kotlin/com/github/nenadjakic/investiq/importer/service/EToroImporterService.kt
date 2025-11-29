package com.github.nenadjakic.investiq.importer.service

import com.github.nenadjakic.investiq.data.entity.asset.AssetAlias
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Tag
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.data.repository.AssetAliasRepository
import com.github.nenadjakic.investiq.data.repository.CurrencyRepository
import com.github.nenadjakic.investiq.data.repository.StagingTransactionRepository
import com.github.nenadjakic.investiq.data.repository.TagRepository
import com.github.nenadjakic.investiq.importer.enum.EToroAction
import com.github.nenadjakic.investiq.importer.model.*
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.text.toDoubleOrNull

@Service("eToroImporterService")
class EToroImporterService(
    private val assetAliasRepository: AssetAliasRepository,
    private val currencyRepository: CurrencyRepository,
    private val stagingTransactionRepository: StagingTransactionRepository,
    private val tagRepository: TagRepository
) : ImporterService<EToroTrade> {

    companion object {
        val ACTIVITY_REQUIRED_COLUMNS = listOf(
            "Date",
            "Type",
            "Details",
            "Amount",
            "Units / Contracts",
            "Realized Equity Change",
            "Realized Equity",
            "Balance",
            "Position ID",
            "Asset type",
            "NWA"
        )
        val DIVIDENDS_REQUIRED_COLUMNS = listOf(
            "Date of Payment",
            "Instrument Name",
            "Net Dividend Received (USD)",
            "Net dividends",
            "Currency",
            "Franked/Unfranked",
            "Franking Credits (AUD)",
            "Withholding Tax Rate (%)",
            "Withholding Tax Amount (USD)",
            "Position ID",
            "Type",
            "ISIN"
        )
    }

    override fun validateHeaders(input: InputStream): ValidationResult {
        TODO("Not yet implemented")
    }

    override fun import(input: InputStream): ImportResult<EToroTrade> {
        XSSFWorkbook(input).use { workbook ->
            val activitySheet = workbook.sheetIterator().asSequence()
                .firstOrNull { it.sheetName.contains("Account Activity", ignoreCase = true) }

            val dividendSheet = workbook.sheetIterator().asSequence()
                .firstOrNull { it.sheetName.contains("Dividends", ignoreCase = true) }

            if (activitySheet == null || dividendSheet == null) {
                throw IllegalArgumentException("Invalid account statement file.")
            }

            val activityHeaderRow = activitySheet.getRow(0)
            val activityColumnIndex = (0 until activityHeaderRow.lastCellNum).associateBy {
                activityHeaderRow.getCell(it)?.stringCellValue?.trim() ?: ""
            }

            val dividendHeaderRow = dividendSheet.getRow(0)
            val dividendColumnIndex = (0 until dividendHeaderRow.lastCellNum).associateBy {
                dividendHeaderRow.getCell(it)?.stringCellValue?.trim() ?: ""
            }

            val missingActivityHeader = ACTIVITY_REQUIRED_COLUMNS.filter { it !in activityColumnIndex.keys }
            val missingDividendHeader = DIVIDENDS_REQUIRED_COLUMNS.filter { it !in dividendColumnIndex.keys }
            if (missingActivityHeader.isNotEmpty() || missingDividendHeader.isNotEmpty()) {
                throw IllegalStateException(
                    "Missing required columns."
                )
            }

            val errors = mutableListOf<ImportError>()
            val rowResults = mutableListOf<RowResult<EToroTrade>>()

            var total = 0
            var success = 0
            var failed = 0

            val dividendRowByPositionIdAndDate = mutableMapOf<Pair<String, LocalDate>, Row>()
            val commisionRowByActionAndPositionIdAndDate =
                mutableMapOf<Triple<EToroAction, String, LocalDate>, Row>()
            val activityRowByPositionIdAndDate = mutableMapOf<Pair<String, LocalDateTime>, Row>()

            for (rowIndex in 1..dividendSheet.lastRowNum) {
                val row = dividendSheet.getRow(rowIndex) ?: continue

                val positionId = row.getCellByNullableIndex(dividendColumnIndex["Position ID"])!!.stringCellValue
                val date = LocalDate.parse(
                    row.getCellByNullableIndex(dividendColumnIndex["Date of Payment"])!!.stringCellValue,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
                dividendRowByPositionIdAndDate[Pair(positionId, date)] = row
            }

            for (rowIndex in 1..activitySheet.lastRowNum) {
                val row = activitySheet.getRow(rowIndex) ?: continue
                val positionId = row.getCellByNullableIndex(activityColumnIndex["Position ID"])!!.stringCellValue
                val date = LocalDateTime.parse(row.getCellByNullableIndex(activityColumnIndex["Date"])!!.stringCellValue,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                val action =
                    EToroAction.fromValue(row.getCellByNullableIndex(activityColumnIndex["Type"])!!.stringCellValue)
                val details = row.getCellByNullableIndex(activityColumnIndex["Details"])!!.stringCellValue

                if (action == EToroAction.SDRT_FEE || (action == EToroAction.FEE && ("On Open".equals(
                        details, true
                    ) || "On Close".equals(
                        details, true
                    )))
                ) {
                    commisionRowByActionAndPositionIdAndDate[Triple(action, positionId, date.toLocalDate())] = row
                } else {
                    activityRowByPositionIdAndDate[Pair(positionId, date)] = row
                }
            }

            activityRowByPositionIdAndDate.entries.forEachIndexed { index, entry ->
                total++
                val key = entry.key
                val row = entry.value

                try {
                    val action =
                        EToroAction.fromValue(row.getCellByNullableIndex(activityColumnIndex["Type"])!!.stringCellValue)

                    val ticker = if (action == EToroAction.BUY || action == EToroAction.SELL) {
                        row.getCellByNullableIndex(activityColumnIndex["Details"])!!.stringCellValue.getTicker()
                    } else if (action == EToroAction.DIVIDEND) {
                        row.getCellByNullableIndex(activityColumnIndex["Details"])!!.stringCellValue.getTicker()
                    } else {
                        ""
                    }
                    val amount = row.getCellByNullableIndex(activityColumnIndex["Amount"])!!.numericCellValue
                    val units = run {
                        val strUnits = row.getCellByNullableIndex(activityColumnIndex["Units / Contracts"])!!.stringCellValue
                        var result = 0.0
                        try {
                            result = strUnits.toDouble()
                        } catch (_: Exception) {

                        }
                        result
                    }

                    val eToroTrade = EToroTrade(
                        action = action,
                        time = LocalDateTime.parse(row.getCellByNullableIndex(activityColumnIndex["Date"])!!.stringCellValue,
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        ticker = ticker,
                        details = row.getCellByNullableIndex(activityColumnIndex["Details"])!!.stringCellValue,
                        amount = amount,
                        units = units,
                        id = key.first
                    )
                    if (action == EToroAction.DIVIDEND) {
                        val dividendRow =
                            dividendRowByPositionIdAndDate.get(Pair(key.first, key.second.toLocalDate()))!!

                        val withHoldingTaxRate = run {
                            dividendRow
                                .getCellByNullableIndex(dividendColumnIndex["Withholding Tax Rate (%)"])!!
                                .stringCellValue
                                .replace(" ", "")
                                .replace("%", "")
                                .toDouble()
                        }
                        eToroTrade.dividend = EToroDividend(
                            time = eToroTrade.time.toLocalDate()!!,
                            amount = dividendRow.getCellByNullableIndex(dividendColumnIndex["Net Dividend Received (USD)"])!!.numericCellValue,
                            withHoldingTaxAmount = dividendRow.getCellByNullableIndex(dividendColumnIndex["Withholding Tax Amount (USD)"])!!.numericCellValue,
                            withHoldingTaxRate = withHoldingTaxRate,
                            parentId = dividendRow.getCellByNullableIndex(dividendColumnIndex["Position ID"])!!.stringCellValue,
                        )
                    } else if (action == EToroAction.BUY) {
                            commisionRowByActionAndPositionIdAndDate[Triple(EToroAction.FEE,key.first, key.second.toLocalDate())]
                                ?.let {
                                    val amount = it.getCellByNullableIndex(activityColumnIndex["Amount"])!!.numericCellValue

                                    eToroTrade.fees.add(EToroFee(
                                        time = key.second,
                                        amount = amount
                                    ))
                                }
                    }
                    rowResults.add(
                        RowResult(
                            index - 1, RowStatus.SUCCESS, eToroTrade
                        )
                    )
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
    }


    private inline fun <reified T> cellValue(cell: Cell?): T? {
        if (cell == null) return null

        return when (T::class) {
            String::class -> when (cell.cellType) {
                CellType.STRING -> cell.stringCellValue
                CellType.NUMERIC -> cell.numericCellValue.toString()
                CellType.BOOLEAN -> cell.booleanCellValue.toString()
                else -> null
            }

            Double::class -> when (cell.cellType) {
                CellType.NUMERIC -> cell.numericCellValue
                CellType.STRING -> cell.stringCellValue.toDoubleOrNull()
                else -> null
            }

            Boolean::class -> when (cell.cellType) {
                CellType.BOOLEAN -> cell.booleanCellValue
                CellType.STRING -> cell.stringCellValue.toBooleanStrictOrNull()
                CellType.NUMERIC -> cell.numericCellValue != 0.0
                else -> null
            }

            LocalDate::class -> {
                if (cell.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                    LocalDate.ofInstant(cell.dateCellValue.toInstant(), ZoneId.systemDefault())
                } else if (cell.cellType == CellType.STRING) {
                    try {
                        LocalDate.parse(cell.stringCellValue, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    } catch (e: Exception) {
                        null
                    }
                } else null
            }

            LocalDateTime::class -> {
                if (cell.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                    LocalDateTime.ofInstant(cell.dateCellValue.toInstant(), ZoneId.systemDefault())
                } else if (cell.cellType == CellType.STRING) {
                    try {
                        LocalDateTime.parse(cell.stringCellValue, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    } catch (e: Exception) {
                        null
                    }
                } else null
            }

            else -> null
        } as T?
    }


    private fun addToStaging(rowResults: MutableList<RowResult<EToroTrade>>) {
        val assetAliases = mutableListOf<AssetAlias>()
        val currencies = mutableMapOf<String, Currency>()
        val tags = mutableMapOf<String, Tag>()
        try {
            assetAliases.addAll(
                assetAliasRepository.findAllByPlatform(Platform.ETORO)
            )

            currencies.putAll(
                currencyRepository.findAll().associateBy { it.code!! })

            tags.putAll(
                tagRepository.findAll().associateBy { it.name })

        } catch (ex: Exception) {
            throw RuntimeException("Failed to load account for importing Trading 212 transactions.", ex)
        }

        val stagingTransactions = mutableListOf<StagingTransaction>()

        for (rowResult in rowResults) {
            if (rowResult.status == RowStatus.SUCCESS) {
                stagingTransactions.addAll(
                    rowResult.mappedObjectInfo!!.toStagingTransactions(assetAliases, currencies, tags)
                )
            }
        }
        stagingTransactionRepository.saveAll(stagingTransactions)
    }

}

fun String.getTicker() = this.substringBefore("/")

fun Row.getCellByNullableIndex(index: Int?): Cell? {
    if (index == null) {
        return null
    }
    return this.getCell(index) ?: return null
}

fun Cell.getValueAsLocalDate(pattern: String = "dd/MM/yyyy"): LocalDate? {
    return when (this.cellType) {
        CellType.NUMERIC if DateUtil.isCellDateFormatted(this) -> {
            LocalDate.ofInstant(this.dateCellValue.toInstant(), ZoneId.systemDefault())
        }

        CellType.STRING -> {
            try {
                LocalDate.parse(this.stringCellValue, DateTimeFormatter.ofPattern(pattern))
            } catch (_: Exception) {
                null
            }
        }

        else -> null
    }
}

inline fun <reified T> Cell.getCell(): T? {
    if (this == null) return null

    return when (T::class) {
        String::class -> when (this.cellType) {
            CellType.STRING -> this.stringCellValue
            CellType.NUMERIC -> this.numericCellValue.toString()
            CellType.BOOLEAN -> this.booleanCellValue.toString()
            else -> null
        }

        Double::class -> when (this.cellType) {
            CellType.NUMERIC -> this.numericCellValue
            CellType.STRING -> this.stringCellValue.toDoubleOrNull()
            else -> null
        }

        Boolean::class -> when (this.cellType) {
            CellType.BOOLEAN -> this.booleanCellValue
            CellType.STRING -> this.stringCellValue.toBooleanStrictOrNull()
            CellType.NUMERIC -> this.numericCellValue != 0.0
            else -> null
        }

        LocalDate::class -> {
            if (this.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(this)) {
                LocalDate.ofInstant(this.dateCellValue.toInstant(), ZoneId.systemDefault())
            } else if (this.cellType == CellType.STRING) {
                try {
                    LocalDate.parse(this.stringCellValue, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } catch (e: Exception) {
                    null
                }
            } else null
        }

        LocalDateTime::class -> {
            if (this.cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(this)) {
                LocalDateTime.ofInstant(this.dateCellValue.toInstant(), ZoneId.systemDefault())
            } else if (this.cellType == CellType.STRING) {
                try {
                    LocalDateTime.parse(this.stringCellValue, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                } catch (e: Exception) {
                    null
                }
            } else null
        }

        else -> null
    } as T?
}