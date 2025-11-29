package com.github.nenadjakic.investiq.importer.command

import com.github.nenadjakic.investiq.commonservice.service.AssetService
import com.github.nenadjakic.investiq.importer.util.MessageType
import com.github.nenadjakic.investiq.importer.util.PrettyPrinter
import org.jline.utils.AttributedStyle
import org.springframework.shell.standard.ShellCommandGroup
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellCommandGroup("Asset commands")
@ShellComponent
class AssetCommand(
    private val prettyPrinter: PrettyPrinter,
    private val assetService: AssetService
) {

    @ShellMethod("List assets filtered by symbol, optionally by currency and exchange", key = ["asset-filter"])
    fun listAssets(
        @ShellOption(value = ["--symbol", "-s"], help = "Ticker symbol", defaultValue = ShellOption.NULL) symbol: String?,
        @ShellOption(value = ["--currency", "-c"], help = "Currency code", defaultValue = ShellOption.NULL) currency: String?,
        @ShellOption(value = ["--exchange", "-e"], help = "Exchange code (acronym)", defaultValue = ShellOption.NULL) exchange: String?
    ) {
        val assets = assetService.findAll(symbol, currency, exchange)
        if (assets.isEmpty()) {
            prettyPrinter.print("No assets for given parameters", AttributedStyle.YELLOW)
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
}