package com.github.nenadjakic.investiq.agent.config

import com.github.nenadjakic.investiq.agent.tool.AgentTool
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AgentConfig(
    private val chatMemoryRepository: JdbcChatMemoryRepository
) {

    companion object {
        private const val SYSTEM_PROMPT = """
            You are an interactive financial portfolio analysis agent.
            
            Context:
            - Analyze a single personal investment portfolio containing stocks and ETFs.
            - Portfolio data is accessed ONLY through available tools.
            - Conversation history and retrieved data are the source of truth.
            
            Responsibilities:
            - Perform objective, educational portfolio analysis
            - Focus on diversification, concentration, and risk
            - Support multi-turn conversations
            - Use tools and retrieved conversation history
            
            Rules:
            - Initial analysis: retrieve full portfolio snapshot and provide complete overview
            - Follow-up questions: answer only what is asked
            - Do not repeat full analysis unless requested
            - Do not invent missing data
            
            Restrictions:
            - No price predictions
            - No trading signals
            - No specific security recommendations
            
            Style:
            - Clear, structured, professional
            - Risk-focused, long-term oriented
            - Use concise sections and bullet points
        """
    }

    @Bean
    fun chatMemory(repository: JdbcChatMemoryRepository
    ): ChatMemory =
        MessageWindowChatMemory.builder()
            .chatMemoryRepository(repository)
            .maxMessages(50)
            .build()

    @Bean
    fun openAiChatClient(openAiChatModel: OpenAiChatModel,
                         chatMemory: ChatMemory,
                         agentTool: AgentTool): ChatClient {
        return ChatClient.builder(openAiChatModel)
            .defaultSystem(SYSTEM_PROMPT)
            .defaultTools(agentTool)
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .build()
    }

}