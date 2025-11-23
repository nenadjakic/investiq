package com.github.nenadjakic.investiq.importer.command

import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.entity.transaction.StagingTransaction
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.importer.model.StagingTransactionResponse
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
    private val stagingTransactionService: StagingTransactionService) {

    private var currentStaging: StagingTransactionResponse? = null

    @ShellMethod("List staging transactions (default unresolved only)")
    fun listStaging(
        @ShellOption(value = ["--unresolved", "-u"], help = "Filter by unresolved status") unresolved: Boolean,
        @ShellOption(value = ["--platform", "-t"], help = "Type of platform (e.g., TRADING212)") platform: Platform,
        @ShellOption(value = ["--limit", "-l"], defaultValue = "25", help = "Limit number of results") limit: Int? = null
    ) {
        stagingTransactionService.listStagingTransactions(unresolved, platform, limit)
            .forEach { stagingTransaction ->
                if (stagingTransaction.importStatus == ImportStatus.IMPORTED) {
                    prettyPrinter.print(stagingTransaction.toString(), MessageType.SUCCESS)
                } else {
                    if (stagingTransaction.resolvedAsset == null) {
                        prettyPrinter.print(stagingTransaction.toString(), MessageType.ERROR)
                    } else {
                        prettyPrinter.print(stagingTransaction.toString(), MessageType.WARNING)
                    }
                }
            }
    }

    @ShellMethod("Review a staging transaction by ID and set as current")
    @ShellMethodAvailability("isStagingSelected")
    fun review(stagingId: UUID) {
        val staging = stagingTransactionService.findById(stagingId)
        currentStaging = staging
        prettyPrinter.print("Now reviewing staging transaction: $stagingId", MessageType.INFO)
    }

    fun isStagingSelected(): Availability {
        return if (currentStaging != null) {
            Availability.available()
        } else {
            Availability.unavailable("No staging transaction selected. Use `review <stagingId>` first.")
        }
    }
}