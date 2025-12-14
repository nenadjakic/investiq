package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.AssetResponse
import com.github.nenadjakic.investiq.service.AssetService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Tag(name = "Asset Controller", description = "Endpoints for managing assets")
@RestController
@RequestMapping("/asset")
class AssetController(
    private val assetService: AssetService
) {

    @Operation(
        summary = "Find all assets",
        description = "Returns a paginated list of assets filtered by optional parameters",
        operationId = "findAllAssets",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Paginated list of assets"
            )
        ]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE ])
    fun findAll(
        symbol: String?,
        currency: String?,
        exchange: String?,
        company: String?,
        @ParameterObject @PageableDefault(size = 25) pageable: Pageable
    ): ResponseEntity<Page<AssetResponse>> {
            return ResponseEntity.ok(
                assetService.findAllPageable(symbol, currency, exchange, company, pageable)
            )
    }

    @Operation(
        summary = "Find all assets",
        description = "Returns a list of assets.",
        operationId = "findAllAssetsAsList",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of assets"
            )
        ]
    )
    @GetMapping(value = [ "/list" ], produces = [ MediaType.APPLICATION_JSON_VALUE ])
    fun findAll(
    ): ResponseEntity<List<AssetResponse>> {
        return ResponseEntity.ok(
            assetService.findAll()
        )
    }
}