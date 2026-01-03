package com.github.nenadjakic.investiq.scheduler.service

import jakarta.mail.internet.MimeMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Value("\${investiq.scheduler.report.recipient-email}")
    private lateinit var recipientEmail: String

    @Value("\${spring.mail.username}")
    private lateinit var fromEmail: String

    /**
     * Send portfolio period report via email
     * @param pdfFilePath Path to the PDF file
     * @param reportDate The date of the report
     * @param periodType The type of period: W (weekly), M (monthly), Y (yearly)
     */
    fun sendPortfolioPeriodReport(pdfFilePath: String, reportDate: LocalDate, periodType: String) {
        log.info("Sending portfolio period report via email to: $recipientEmail")

        try {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setFrom(fromEmail)
            helper.setTo(recipientEmail)

            val periodName = when (periodType) {
                "W" -> "Weekly"
                "M" -> "Monthly"
                "Y" -> "Yearly"
                else -> "Period"
            }

            helper.setSubject("$periodName Portfolio Report - ${reportDate.format(DateTimeFormatter.ISO_DATE)}")

            val emailBody = buildEmailBody(reportDate, periodType)
            helper.setText(emailBody, true)

            // Attach PDF file
            val file = File(pdfFilePath)
            if (file.exists()) {
                val attachment = FileSystemResource(file)
                helper.addAttachment(file.name, attachment)
                log.info("Attached PDF file: ${file.name}")
            } else {
                log.warn("PDF file not found: $pdfFilePath")
                throw IllegalStateException("PDF file not found: $pdfFilePath")
            }

            mailSender.send(message)
            log.info("Email sent successfully to: $recipientEmail")

        } catch (e: Exception) {
            log.error("Error sending email to $recipientEmail", e)
            throw RuntimeException("Failed to send email", e)
        }
    }

    private fun buildEmailBody(reportDate: LocalDate, periodType: String): String {
        val periodName = when (periodType) {
            "W" -> "Weekly"
            "M" -> "Monthly"
            "Y" -> "Yearly"
            else -> "Period"
        }

        val dateStr = reportDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .footer { background-color: #f1f1f1; padding: 10px; text-align: center; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>$periodName Portfolio Report</h1>
                </div>
                <div class="content">
                    <p>Dear Investor,</p>
                    <p>Please find attached your $periodName portfolio report for <strong>$dateStr</strong>.</p>
                    <p>This report contains a comprehensive overview of your portfolio performance for the specified period.</p>
                    <p>If you have any questions or concerns, please don't hesitate to contact us.</p>
                    <br>
                    <p>Best regards,<br>InvestIQ Team</p>
                </div>
                <div class="footer">
                    <p>This is an automated message. Please do not reply to this email.</p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}

