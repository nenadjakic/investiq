package com.github.nenadjakic.investiq.importer.command

import com.github.nenadjakic.investiq.common.dto.StagingTransactionResponse
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.service.StagingTransactionService
import com.github.nenadjakic.investiq.importer.util.MessageType
import com.github.nenadjakic.investiq.importer.util.PrettyPrinter
import com.github.nenadjakic.investiq.service.AssetService
import org.jline.utils.AttributedStyle
import org.springframework.data.domain.Pageable
import org.springframework.shell.Availability
import org.springframework.shell.standard.*

@ShellCommandGroup("Staging transaction commands")
@ShellComponent
class StagingTransactionCommand(
    private val prettyPrinter: PrettyPrinter,
    private val stagingTransactionService: StagingTransactionService,
    private val assetService: AssetService
) {

    private var currentStaging: StagingTransactionResponse? = null

    @ShellMethod(value = "List staging transactions", key = ["staging-transaction-list"])
    fun listStagings(
        @ShellOption(
            value = ["--unresolved-only", "-u"],
            defaultValue = "true",
            help = "Filter by unresolved status"
        ) unresolved: Boolean,
        @ShellOption(value = ["--platform", "-t"], help = "Type of platform (e.g., TRADING212)") platform: Platform,
        @ShellOption(
            value = ["--limit", "-l"],
            defaultValue = "25",
            help = "Limit number of results"
        ) limit: Int? = null
    ) {
        val stagingTransactions = stagingTransactionService.findAll(platform, if (unresolved) ImportStatus.PENDING else null,
            Pageable.ofSize(limit!!))
        if (stagingTransactions.isEmpty) {
            prettyPrinter.print("No staging transaction for given parameters", AttributedStyle.YELLOW)
            return
        }
        printStagingHeader()

        stagingTransactions.forEach {

            val foregroundStyle = when {
                it.importStatus == ImportStatus.IMPORTED -> AttributedStyle.GREEN
                it.resolvedAsset == null -> AttributedStyle.RED
                else -> AttributedStyle.YELLOW
            }

            printStaging(it, foregroundStyle)
        }
    }

    @ShellMethod("Review a staging transaction by ID and set as current", key = ["staging-transaction-review"])
    @ShellMethodAvailability("!isStagingSelected")
    fun review(@ShellOption(value = ["--id", "-i"], help = "Staging transaction id") stagingId: UUID) {
        runCatching {
            currentStaging = stagingTransactionService.findById(stagingId)
            printStagingHeader(AttributedStyle.BLUE)
            printStaging(currentStaging!!, AttributedStyle.BLUE)
        }.onFailure {
            prettyPrinter.print(it.message, MessageType.ERROR)
        }.onSuccess {}
    }

    @ShellMethod("Show the currently selected staging transaction", key = ["staging-transaction-current"])
    @ShellMethodAvailability("isStagingSelected")
    fun current() {
        prettyPrinter.print("Current staging transaction:", AttributedStyle.BLUE)
        printStagingHeader(AttributedStyle.BLUE)
        printStaging(currentStaging!!, AttributedStyle.BLUE)
    }


    @ShellMethod(
        "Assign an existing asset to the current staging transaction by Asset UUID",
        key = ["staging-transaction-assign-asset"]
    )
    @ShellMethodAvailability("isStagingSelected")
    fun assignAsset(assetId: UUID) {
        runCatching {
            currentStaging = stagingTransactionService.assignAsset(currentStaging!!.id, assetId)
            prettyPrinter.print("Successfuly assign asset", AttributedStyle.BLUE)
            printStagingHeader(AttributedStyle.BLUE)
            printStaging(currentStaging!!, AttributedStyle.BLUE)

        }.onFailure {
            prettyPrinter.print(it.message, MessageType.ERROR)
        }
    }

    @ShellMethod("Confirm and finish the current review", key = ["staging-transaction-confirm"])
    @ShellMethodAvailability("isStagingSelected")
    fun confirmReview() {
        prettyPrinter.print(
            "Review finished for staging transaction ${currentStaging!!.id}", MessageType.SUCCESS
        )

        runCatching {
            stagingTransactionService.updateStatus(currentStaging!!.id, ImportStatus.VALIDATED)
        }.onFailure {
            prettyPrinter.print(it.message, MessageType.ERROR)
        }.onSuccess { currentStaging = null }
    }

    @ShellMethod(
        "Confirm and finish review for multiple staging transactions", key = ["staging-transaction-confirm-list"]
    )
    fun confirmReviewList(
        @ShellOption(value = ["--ids", "-i"], help = "List of staging transaction IDs. Max 10 element") ids: Array<UUID>
    ) {
        if (ids.isEmpty()) {
            prettyPrinter.print("No IDs provided.", MessageType.ERROR)
            return
        }

        prettyPrinter.print("Batch updating ${ids.size} staging transactions...", AttributedStyle.BLUE)

        runCatching {
            stagingTransactionService.updateStatus(ids.toList(), ImportStatus.VALIDATED)

            prettyPrinter.print("Batch operation completed.", AttributedStyle.BLUE)
        }.onFailure {
            prettyPrinter.print(it.message, MessageType.ERROR)
        }
    }

    fun isStagingSelected(): Availability {
        return if (currentStaging != null) {
            Availability.available()
        } else {
            Availability.unavailable("No staging transaction selected. Use `review <stagingId>` first.")
        }
    }

    private fun printStagingHeader(foregroundStyle: Int? = null, backgroundStyle: Int? = null) {
        val header = String.format(
            "%-36s %-30s %-10s %-8s %-25s %-10s", "ID", "Date", "Type", "Symbol", "Resolved Asset", "Status"
        )

        prettyPrinter.print(header, foregroundStyle)
        prettyPrinter.print("-".repeat(header.length), foregroundStyle)
    }

    private fun printStaging(
        staging: StagingTransactionResponse,
        foregroundStyle: Int? = null,
        backgroundStyle: Int? = null
    ) {
        val resolvedAssetDisplay = staging.resolvedAsset?.let { asset ->
            "${asset.symbol} ${asset.currency} @${asset.exchange}"
        } ?: "-"

        val line = String.format(
            "%-36s %-30s %-10s %-8s %-25s %-10s",
            staging.id,
            staging.date,
            staging.type,
            staging.symbol ?: "-",
            resolvedAssetDisplay,
            staging.importStatus
        )
        prettyPrinter.print(line, foregroundStyle)
    }

}