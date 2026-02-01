package com.github.nenadjakic.investiq.restapp.controller

import com.github.nenadjakic.investiq.common.dto.asset.AssetAddRequest
import com.github.nenadjakic.investiq.common.dto.asset.AssetResponse
import com.github.nenadjakic.investiq.service.AssetService
import io.swagger.v3.oas.annotations.Operation
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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder


@Tag(name = "Asset Controller", description = "Endpoints for managing assets")
@RestController
@RequestMapping("/asset")
@Validated
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

    @Operation(
        summary = "Find asset by ID",
        description = "Returns an asset by its ID",
        operationId = "findAssetById",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Asset found"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Asset not found"
            )
        ]
    )
    @GetMapping(value = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable id: java.util.UUID): ResponseEntity<AssetResponse> =
        ResponseEntity.ofNullable(assetService.findById(id))

    @Operation(
        summary = "Create a new asset",
        description = "Creates a new asset and returns 201 Created with the location of the new resource",
        operationId = "createAsset",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Asset created successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input data"
            )
        ]
    )
    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@Valid @RequestBody request: AssetAddRequest): ResponseEntity<Void> {
        assetService.create(request)
            .let {
                val location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(it.id)
                .toUri()
                return ResponseEntity.created(location).build()
            }
    }
}