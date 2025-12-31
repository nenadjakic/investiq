package com.github.nenadjakic.investiq.agent.service

import com.github.nenadjakic.investiq.agent.dto.ChatMessageRequest
import com.github.nenadjakic.investiq.agent.dto.ChatMessageResponse
import com.github.nenadjakic.investiq.agent.dto.ChatStartResponse
import org.springframework.stereotype.Service
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import java.util.UUID

@Service
class ChatService(
    private val chatClient: ChatClient,
) {

    companion object {
        val INITIAL_USER_MESSAGE = """
            Perform an initial analysis of my investment portfolio using the complete portfolio snapshot from the available tools.

            Focus specifically on:
            - Diversification across sectors, geographies, and holdings
            - Concentration risks
            - Overall portfolio risk characteristics
            
            Interpret the data rather than listing it.
            Present the analysis in a clear, structured, and professional format.
            
            Use short sections with bullet points and include a brief summary at the end.
        """.trimIndent()
    }

    fun startConversation(): ChatStartResponse {
        val conversationId = UUID.randomUUID().toString()
        val reply = chatClient
            .prompt()
            .advisors { it.param(ChatMemory.CONVERSATION_ID, conversationId) }
            .user(INITIAL_USER_MESSAGE)
            .call()
            .chatResponse()

        if (reply != null && reply.result.output.text != null) {
            return ChatStartResponse(conversationId, reply.result.output.text!!)
        } else {
            throw RuntimeException("AI returned no content")
        }
    }

    fun sendMessage(conversationId: String, request: ChatMessageRequest): ChatMessageResponse {
        val userText = request.text

        val reply = chatClient
            .prompt()
            .advisors { it.param(ChatMemory.CONVERSATION_ID, conversationId) }
            .user(request.text)
            .call()
            .chatResponse()

        if (reply != null && reply.result.output.text != null) {
            return ChatMessageResponse(conversationId, reply.result.output.text!!)
        } else {
            throw RuntimeException("AI returned no content")
        }
    }
}
