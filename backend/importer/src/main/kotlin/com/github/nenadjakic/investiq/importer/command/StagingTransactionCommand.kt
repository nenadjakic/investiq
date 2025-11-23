package com.github.nenadjakic.investiq.importer.command

import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.importer.model.StagingTransactionResponse
import com.github.nenadjakic.investiq.importer.service.AssetService
import com.github.nenadjakic.investiq.importer.service.StagingTransactionService
import com.github.nenadjakic.investiq.importer.util.MessageType
import com.github.nenadjakic.investiq.importer.util.PrettyPrinter
import org.springframework.shell.Availability
import org.springframework.shell.standard.ShellCommandGroup
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellMethodAvailability
import org.springframework.shell.standard.ShellOption
import java.util.UUID

@ShellCommandGroup("Staging transaction commands")
@ShellComponent
class StagingTransactionCommand(
    private val prettyPrinter: PrettyPrinter,
    private val stagingTransactionService: StagingTransactionService,
    private val assetService: AssetService) {

    private var currentStaging: StagingTransactionResponse? = null

    @ShellMethod("List assets filtered by symbol, optionally by currency and exchange")
    fun listAssets(
        @ShellOption(value = ["--symbol", "-s"], help = "Ticker symbol") symbol: String,
        @ShellOption(value = ["--currency", "-c"], help = "Currency code", defaultValue = ShellOption.NULL) currency: String?,
        @ShellOption(value = ["--exchange", "-e"], help = "Exchange code (acronym)", defaultValue = ShellOption.NULL) exchange: String?
    ) {
        val assets = assetService.findAll(symbol, currency, exchange)
        if (assets.isEmpty()) {
            println("no assets")
            return
        }

        val header = String.format(
            "%-36s %-10s %-10s %-10s",
            "ID", "Symbol", "Currency", "Exchange"
        )

        prettyPrinter.print(header, MessageType.INFO)
        prettyPrinter.print("-".repeat(header.length), MessageType.INFO)

        assets.forEach {
            val line = String.format(
                "%-36s %-10s %-10s %-10s",
                it.id,
                it.symbol,
                it.currency,
                it.exchange
            )
            prettyPrinter.print(line, MessageType.INFO)
        }

    }

    @ShellMethod("List staging transactions")
    fun listStaging(
        @ShellOption(value = ["--unresolved", "-u"], help = "Filter by unresolved status") unresolved: Boolean,
        @ShellOption(value = ["--platform", "-t"], help = "Type of platform (e.g., TRADING212)") platform: Platform,
        @ShellOption(value = ["--limit", "-l"], defaultValue = "25", help = "Limit number of results") limit: Int? = null
    ) {
        val stagingTransactions = stagingTransactionService.listStagingTransactions(unresolved, platform, limit)
        if (stagingTransactions.isEmpty()) {
            println("no staging trans")
            return
        }
        val header = String.format(
            "%-36s %-20s %-10s %-8s %-25s %-10s",
            "ID", "Date", "Type", "Symbol", "Resolved Asset", "Status"
        )
        prettyPrinter.print(header, MessageType.INFO)
        prettyPrinter.print("-".repeat(header.length), MessageType.INFO)

        stagingTransactions.forEach {
            val resolvedAssetDisplay = it.resolvedAsset?.let { asset ->
                "${asset.symbol} ${asset.currency} @${asset.exchange}"
            } ?: "-"

            val line = String.format(
                "%-36s %-20s %-10s %-8s  %-25s %-10s",
                it.id,
                it.date,
                it.type,
                it.symbol ?: "-",
                resolvedAssetDisplay,
                it.importStatus
            )

            val messageType = when {
                it.importStatus == ImportStatus.IMPORTED -> MessageType.SUCCESS
                it.resolvedAsset == null -> MessageType.ERROR
                else -> MessageType.WARNING
            }

            prettyPrinter.print(line, messageType)
        }
    }

    @ShellMethod("Review a staging transaction by ID and set as current")
    @ShellMethodAvailability("!isStagingSelected")
    fun review(stagingId: UUID) {
        val staging = stagingTransactionService.findById(stagingId)
        if (staging == null) {
            prettyPrinter.print("Staging transaction with ID $stagingId not found.", MessageType.ERROR)
            return
        }
        currentStaging = staging

        val resolvedAssetDisplay = staging.resolvedAsset?.let { asset ->
            "${asset.symbol} ${asset.currency} @${asset.exchange}"
        } ?: "-"

        val line = String.format(
            "%-36s %-20s %-10s %-8s  %-25s %-10s",
            staging.id,
            staging.date,
            staging.type,
            staging.symbol ?: "-",
            resolvedAssetDisplay,
            staging.importStatus
        )

        prettyPrinter.print("Now reviewing staging transaction:", MessageType.INFO)
        prettyPrinter.print(line, MessageType.INFO)
    }

    @ShellMethod("Assign an existing asset to the current staging transaction by Asset UUID")
    @ShellMethodAvailability("isStagingSelected")
    fun assignAsset(assetId: UUID) {
        val staging = currentStaging ?: run {
            prettyPrinter.print("No staging transaction selected!", MessageType.ERROR)
            return
        }
/*
        val asset = stagingTransactionService.findAssetById(assetId)
        if (asset == null) {
            prettyPrinter.print("Asset with ID $assetId not found.", MessageType.ERROR)
            return
        }

        staging.resolvedAsset = asset
        staging.importStatus = ImportStatus.VALIDATED
        stagingTransactionService.save(staging.toEntity())

        prettyPrinter.print(
            "Assigned asset ${asset.symbol} ${asset.currency} @${asset.exchange} to staging transaction ${staging.id}",
            MessageType.SUCCESS
        )

 */
    }

    fun isStagingSelected(): Availability {
        return if (currentStaging != null) {
            Availability.available()
        } else {
            Availability.unavailable("No staging transaction selected. Use `review <stagingId>` first.")
        }
    }
}