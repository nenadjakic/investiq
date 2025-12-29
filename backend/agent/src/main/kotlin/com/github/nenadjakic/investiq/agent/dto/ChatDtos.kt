package com.github.nenadjakic.investiq.agent.dto

data class ChatStartResponse(
    val conversationId: String,
    val message: String
)

data class ChatMessageRequest(
    val text: String
)

data class ChatMessageResponse(
    val conversationId: String,
    val message: String
)

