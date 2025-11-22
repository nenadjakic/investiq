package com.github.nenadjakic.investiq.importer.util

import org.jline.terminal.Terminal
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component


@Component
class PrettyPrinter @Autowired constructor(@Lazy private val terminal: Terminal) {
    fun getColored(message: String?, attributedStyle: Int): String {
        return (AttributedStringBuilder())
            .append(message, AttributedStyle.DEFAULT.foreground(attributedStyle))
            .toAnsi()
    }

    fun info(message: String?): String {
        return getColored(message, AttributedStyle.BLACK)
    }

    fun success(message: String?): String {
        return getColored(message, AttributedStyle.GREEN)
    }

    fun warning(message: String?): String {
        return getColored(message, AttributedStyle.YELLOW)
    }

    fun error(message: String?): String {
        return getColored(message, AttributedStyle.RED)
    }

    fun print(message: String?, attributedStyle: Int) {
        val toPrint = getColored(message, attributedStyle)

        terminal.writer().println(toPrint)
        terminal.flush()
    }
}