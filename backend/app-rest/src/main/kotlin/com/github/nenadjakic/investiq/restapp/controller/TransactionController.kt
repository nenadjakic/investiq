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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}
