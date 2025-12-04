package com.github.nenadjakic.investiq.importer.command

import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.service.model.importer.EToroTrade
import com.github.nenadjakic.investiq.service.model.importer.IBKRTrade
import com.github.nenadjakic.investiq.service.model.importer.Trading212Trade
import com.github.nenadjakic.investiq.service.importer.ImporterService
import com.github.nenadjakic.investiq.service.importer.RevolutImporterService
import com.github.nenadjakic.investiq.importer.util.PrettyPrinter
import org.jline.utils.AttributedStyle
import org.slf4j.LoggerFactory
import org.springframework.shell.standard.ShellCommandGroup
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.nio.file.Files
import java.nio.file.Paths

@ShellCommandGroup("Importer commands")
@ShellComponent
class ImporterCommand(
    private val prettyPrinter: PrettyPrinter,
    private val tradingImporterService: ImporterService<Trading212Trade>,
    private val eToroImporterService: ImporterService<EToroTrade>,
    private val ibkrImportesService: ImporterService<IBKRTrade>,
    private val revolutImporterService: RevolutImporterService
) {
    companion object {
        private val log = LoggerFactory.getLogger(ImporterCommand::class.java)
    }

    @ShellMethod(value = "Imports a CSV file")
    fun import(
        @ShellOption(value = ["--platform", "-t"], help = "Type of platform (e.g., TRADING212)") platform: Platform,
        @ShellOption(value = ["--path", "-p"], help = "Path to the file") path: String
    ) {
        val filePath = runCatching { Paths.get(path) }.getOrNull()?.takeIf { Files.exists(it) }
        if (filePath == null) {
            log.error("File not found at path: $path")
            return
        }
        val result = Files.newInputStream(filePath).use {

           val result = when (platform) {
                Platform.ETORO -> {
                    eToroImporterService.import(it)
                }

                Platform.TRADING212 -> {
                    tradingImporterService.import(it)
                }

                Platform.REVOLUT -> {
                    revolutImporterService.import(it)
                }
                Platform.IBKR -> {
                    ibkrImportesService.import(it)
                }
            }

            result
        }

        result.also { result ->
            prettyPrinter.print(
                "Import finished for platform   : $platform — " +
                        "${result.summary.successfulRows} successful, " +
                        "${result.summary.failedRows} failed", AttributedStyle.GREEN
            )
        }.also { result ->
            result.errors.forEach { error ->
                prettyPrinter.print("Row ${error.rowIndex} error: ${error.message}", AttributedStyle.RED)
            }
        }
    }
}
