package com.github.nenadjakic.investiq.agent.controller

import com.github.nenadjakic.investiq.agent.dto.ChatMessageRequest
import com.github.nenadjakic.investiq.agent.dto.ChatMessageResponse
import com.github.nenadjakic.investiq.agent.dto.ChatStartResponse
import com.github.nenadjakic.investiq.agent.service.ChatService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
@Tag(
    name = "Chat Controller",
    description = "Endpoints for managing chat conversations"
)
class ChatController(
    private val chatService: ChatService
) {

    @Operation(
        operationId = "startConversation",
        summary = "Start a new conversation",
        description = "Creates a new chat conversation and returns its identifier"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Conversation successfully started",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChatStartResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    @PostMapping("/start")
    fun startConversation(): ChatStartResponse {
        return chatService.startConversation()
    }

    @Operation(
        summary = "Send a message to a conversation",
        description = "Sends a message to an existing chat conversation"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Message successfully processed",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ChatMessageResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Conversation not found"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    @PostMapping("/{conversationId}/message")
    fun sendMessage(@PathVariable conversationId: String, @RequestBody request: ChatMessageRequest): ChatMessageResponse {
        return chatService.sendMessage(conversationId, request)
    }
}

