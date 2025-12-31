package com.github.nenadjakic.investiq.agent.service

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ai.chat.client.ChatClient

@Service
class PortfolioChatService {

    @Autowired
    private lateinit var chatClient: ChatClient


    fun analyzeWithAi(): String {
        val userMessage = """
            Perform an initial analysis of my portfolio using the full snapshot of holdings from the available tool.
            Focus on diversification, concentration, and risk, providing structured, professional insights. 
            Do not just display the data—interpret it.
        """.trimIndent()

        // Synchronous call to the chat client - use blocking call via .chat() and extract content
        val response = try {
            val reply = chatClient.prompt().user(userMessage).call()
            reply?.content() ?: "AI returned no content"
        } catch (ex: Exception) {
            "AI analysis failed: ${ex.message}"
        }

        return response
    }
}
