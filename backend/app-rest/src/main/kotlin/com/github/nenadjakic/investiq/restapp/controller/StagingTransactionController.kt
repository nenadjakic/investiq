package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.StagingTransactionEditRequest
import com.github.nenadjakic.investiq.common.dto.StagingTransactionResponse
import com.github.nenadjakic.investiq.data.entity.transaction.ImportStatus
import com.github.nenadjakic.investiq.data.enum.Platform
import com.github.nenadjakic.investiq.service.StagingTransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Tag(name = "Staging Transaction Controller", description = "Endpoints for managing staging transaction")
@RestController
@RequestMapping("/staging-transaction")
@Validated
class StagingTransactionController(
    private val stagingTransactionService: StagingTransactionService
) {

    @Operation(
        summary = "Import staging transactions (file upload)",
        description = "Upload a file for a given platform to import staging transactions. Returns 201 Created on success.",
        responses = [
            ApiResponse(responseCode = "201", description = "Import accepted/created"),
            ApiResponse(responseCode = "400", description = "Bad request / validation error"),
            ApiResponse(responseCode = "415", description = "Unsupported media type", content = [Content()]),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun import(
        @RequestParam("platform") platform: Platform,
        @RequestPart("file") multipartFile: MultipartFile) {
    }

    @Operation(
        summary = "Find all staging transactions",
        description = "Return a paginated list of staging transactions filtered by optional parameters",
        operationId = "findAllStagingTransactions",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Paginated list of staging transactions"
            ),
            ApiResponse(responseCode = "400", description = "Bad request")
        ]
    )
    @GetMapping(produces = [ MediaType.APPLICATION_JSON_VALUE ])
    fun findAll(@RequestParam platform: Platform?,
                @RequestParam importStatus: ImportStatus?,
                @ParameterObject @PageableDefault(size = 25) pageable: Pageable): ResponseEntity<Page<StagingTransactionResponse>> {
        return ResponseEntity.ok(stagingTransactionService.findAll(platform, importStatus, pageable))
    }

    @Operation(
        summary = "Find staging transaction by id",
        description = "Return a staging transactions filtered by id",
        operationId = "findStagingTransactionById",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Staging transaction"
            ),
            ApiResponse(responseCode = "404", description = "Not found")
        ]
    )
    @GetMapping(value = ["/{id}"], produces = [ MediaType.APPLICATION_JSON_VALUE ])
    fun findById(@PathVariable id: UUID): ResponseEntity<StagingTransactionResponse> {
        return ResponseEntity.ok(stagingTransactionService.findById(id))
    }

    @Operation(
        summary = "Update staging transaction",
        description = "Update a single staging transaction using DTO",
        operationId = "updateStagingTransaction",
        responses = [
            ApiResponse(responseCode = "204", description = "Updated successfully"),
            ApiResponse(responseCode = "400", description = "Validation error"),
            ApiResponse(responseCode = "404", description = "Not found")
        ]
    )
    @PutMapping(produces = [ MediaType.APPLICATION_JSON_VALUE ])
    fun update(@Valid @RequestBody request: StagingTransactionEditRequest): ResponseEntity<Void> {
        stagingTransactionService.update(request)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Bulk update staging transactions status",
        description = "Bulk update status of multiple staging transactions (and related ones), expects list of UUIDs in body",
        operationId = "bulkUpdateStagingTransactionStatus",
        responses = [
            ApiResponse(responseCode = "204", description = "Status updated"),
            ApiResponse(responseCode = "400", description = "Bad request"),
            ApiResponse(responseCode = "404", description = "One or more ids not found")
        ]
    )
    @PutMapping(value = ["/status"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@RequestBody ids: List<UUID>): ResponseEntity<Void> {
        stagingTransactionService.updateStatus(ids, ImportStatus.VALIDATED)
        return ResponseEntity.noContent().build()
    }
}