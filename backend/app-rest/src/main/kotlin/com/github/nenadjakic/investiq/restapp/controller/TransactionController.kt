package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.TransactionResponse
import com.github.nenadjakic.investiq.service.TransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Transaction Controller", description = "Endpoints for managing transactions")
@RestController
@RequestMapping("/transaction")
class TransactionController(
    private val transactionService: TransactionService
) {

    @Operation(
        summary = "Find all transactions",
        description = "Returns a paginated list of transactions",
        operationId = "findAllTransactions",
        responses = [
            ApiResponse(responseCode = "200", description = "Paginated list of transactions")
        ]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findAll(
        @ParameterObject @PageableDefault(size = 50, sort = ["date"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TransactionResponse>> {
        return ResponseEntity.ok(transactionService.findAll(pageable))
    }

    @Operation(
        summary = "Find last N transactions",
        description = "Returns the last N transactions; defaults to 10 when n is not provided",
        operationId = "findLastTransactions",
        responses = [
            ApiResponse(responseCode = "200", description = "List of recent transactions")
        ]
    )
    @GetMapping(path = ["/last"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findLast(@RequestParam(required = false, defaultValue = "10") n: Int): ResponseEntity<List<TransactionResponse>> {
        return ResponseEntity.ok(transactionService.findLast(n))
    }

    @Operation(
        summary = "Copy validated staging transactions to transactions table",
        description = "Copies all validated staging transactions into the main transactions table. Returns 204 No Content on success.",
        operationId = "copyValidatedTransactions",
        responses = [
            ApiResponse(responseCode = "204", description = "Copy successful")
        ]
    )
    @PostMapping("/copy")
    fun copyValidatedTransactions(): ResponseEntity<Void> {
        transactionService.copy()
        return ResponseEntity.noContent().build()
    }
}
