package com.github.nenadjakic.investiq.importer.util

import org.jline.terminal.Terminal
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component


@Component
class PrettyPrinter @Autowired constructor(@Lazy private val terminal: Terminal) {

    fun getColored(
        message: String?,
        foreground: Int? = null,
        background: Int? = null,
        bold: Boolean = false,
        blink: Boolean = false,
        underline: Boolean = false
    ): String {
        var style = AttributedStyle.DEFAULT

        foreground?.let { style = style.foreground(it) }
        background?.let { style = style.background(it) }
        if (bold) style = style.bold()
        if (blink) style = style.blink()
        if (underline) style = style.underline()

        return AttributedStringBuilder()
            .append(message, style)
            .toAnsi()
    }

    fun getColored(message: String?, attributedStyle: Int): String {
        return (AttributedStringBuilder())
            .append(message, AttributedStyle.DEFAULT.foreground(attributedStyle))
            .toAnsi()
    }

    fun info(message: String?): String {
        return getColored(message)
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

    fun print(message: String?, messageType: MessageType) {
        val toPrint = when (messageType) {
            MessageType.INFO -> info(message)
            MessageType.SUCCESS -> success(message)
            MessageType.WARNING -> warning(message)
            MessageType.ERROR -> error(message)
        }
        terminal.writer().println(toPrint)
        terminal.flush()
    }

    fun print(message: String?, foreground: Int? = null, background: Int? = null,) {
        val toPrint = getColored(
            message = message,
            foreground = foreground,
            background = background
        )
        terminal.writer().println(toPrint)
        terminal.flush()
    }
}

enum class MessageType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}