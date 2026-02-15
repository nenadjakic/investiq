package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.transaction.BuyRequest
import com.github.nenadjakic.investiq.common.dto.transaction.SellRequest
import com.github.nenadjakic.investiq.common.dto.transaction.DepositRequest
import com.github.nenadjakic.investiq.common.dto.transaction.WithdrawalRequest
import com.github.nenadjakic.investiq.common.dto.transaction.DividendRequest
import com.github.nenadjakic.investiq.common.dto.transaction.TransactionResponse
import com.github.nenadjakic.investiq.service.TransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Tag(name = "Transaction Controller", description = "Endpoints for managing transactions")
@RestController
@RequestMapping("/transaction")
@Validated
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

    @Operation(
        summary = "Add a buy transaction",
        description = "Creates a new buy transaction and returns the location of the created resource",
        operationId = "addBuyTransaction",
        responses = [
            ApiResponse(responseCode = "201", description = "Buy transaction created"),
            ApiResponse(responseCode = "400", description = "Invalid input data")
        ]
    )
    @PostMapping(path = ["buy"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun addBuyTransaction(@Valid @RequestBody request: BuyRequest): ResponseEntity<Void> {
        transactionService.addBuyTransaction(request).let {
            val location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(it)
                .toUri()
            return ResponseEntity.created(location).build()
        }
    }

    @Operation(
        summary = "Add a sell transaction",
        description = "Creates a new sell transaction and returns the location of the created resource",
        operationId = "addSellTransaction",
        responses = [
            ApiResponse(responseCode = "201", description = "Sell transaction created"),
            ApiResponse(responseCode = "400", description = "Invalid input data")
        ]
    )
    @PostMapping(path = ["sell"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun addSellTransaction(@Valid @RequestBody request: SellRequest): ResponseEntity<Void> {
        transactionService.addSellTransaction(request).let {
            val location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(it)
                .toUri()
            return ResponseEntity.created(location).build()
        }
    }

    @Operation(
        summary = "Add a deposit transaction",
        description = "Creates a new deposit transaction and returns the location of the created resource",
        operationId = "addDepositTransaction",
        responses = [
            ApiResponse(responseCode = "201", description = "Deposit transaction created"),
            ApiResponse(responseCode = "400", description = "Invalid input data")
        ]
    )
    @PostMapping(path = ["deposit"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun addDepositTransaction(@Valid @RequestBody request: DepositRequest): ResponseEntity<Void> {
        transactionService.addDepositTransaction(request).let {
            val location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(it)
                .toUri()
            return ResponseEntity.created(location).build()
        }
    }

    @Operation(
        summary = "Add a withdrawal transaction",
        description = "Creates a new withdrawal transaction and returns the location of the created resource",
        operationId = "addWithdrawalTransaction",
        responses = [
            ApiResponse(responseCode = "201", description = "Withdrawal transaction created"),
            ApiResponse(responseCode = "400", description = "Invalid input data")
        ]
    )
    @PostMapping(path = ["withdrawal"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun addWithdrawalTransaction(@Valid @RequestBody request: WithdrawalRequest): ResponseEntity<Void> {
        transactionService.addWithdrawalTransaction(request).let {
            val location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(it)
                .toUri()
            return ResponseEntity.created(location).build()
        }
    }

    @Operation(
        summary = "Add a dividend transaction",
        description = "Creates a new dividend transaction and returns the location of the created resource",
        operationId = "addDividendTransaction",
        responses = [
            ApiResponse(responseCode = "201", description = "Dividend transaction created"),
            ApiResponse(responseCode = "400", description = "Invalid input data")
        ]
    )
    @PostMapping(path = ["dividend"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun addDividendTransaction(@Valid @RequestBody request: DividendRequest): ResponseEntity<Void> {
        transactionService.addDividendTransaction(request).let {
            val location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(it)
                .toUri()
            return ResponseEntity.created(location).build()
        }
    }
}
