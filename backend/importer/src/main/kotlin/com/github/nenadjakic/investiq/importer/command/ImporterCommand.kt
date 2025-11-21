package com.github.nenadjakic.investiq.importer.command

import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.importer.model.Trading212Trade
import com.github.nenadjakic.investiq.importer.service.ImporterService
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
    private val importerService: ImporterService<Trading212Trade>
) {
    companion object {
        private val log = LoggerFactory.getLogger(ImporterCommand::class.java)
    }

    @ShellMethod(value = "Imports a CSV file")
    fun import(
        @ShellOption(value = ["--platform", "-t"], help = "Type of platform (e.g., TRADING212)") platform: Platform,
        @ShellOption(value = ["--path", "-p"], help = "Path to the file") path: String
    ): Unit {
        val filePath = runCatching { Paths.get(path) }.getOrNull()?.takeIf { Files.exists(it) }
        if (filePath == null) {
            log.error("File not found at path: $path")
            return
        }
        importerService.import(Files.newInputStream(filePath))
            .also { result ->
                log.info(
                    "Import finished for platform   : $platform — " +
                            "${result.summary.successfulRows} successful, " +
                            "${result.summary.failedRows} failed"
                )
            }
    }
}
