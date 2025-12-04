package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.AssetResponse
import com.github.nenadjakic.investiq.common.dto.toAssetResponse
import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.ListedAsset
import com.github.nenadjakic.investiq.data.entity.asset.Stock
import com.github.nenadjakic.investiq.data.entity.core.Company
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Exchange
import com.github.nenadjakic.investiq.data.repository.AssetRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class AssetService(
    private val assetRepository: AssetRepository
) {
    @Transactional
    fun findAll(symbol: String?, currency: String?, exchange: String?, company: String? = null): List<AssetResponse> {
        val spec = getSpecification(symbol, currency, exchange, company)
        return assetRepository.findAll(spec).map { it.toAssetResponse() }
    }

    @Transactional
    fun findAllPageable(
        symbol: String?,
        currency: String?,
        exchange: String?,
        company: String?,
        pageable: Pageable): Page<AssetResponse> {
        val spec = getSpecification(symbol, currency, exchange, company)

        return assetRepository.findAll(spec, pageable).map { it.toAssetResponse() }
    }

    fun findById(assetId: UUID): AssetResponse? =
        assetRepository.findById(assetId)
            .map { it.toAssetResponse() }
            .getOrNull()

    private fun getSpecification(
        symbol: String?,
        currency: String?,
        exchange: String?,
        company: String?
    ): Specification<Asset> {
        return Specification<Asset> { root, query, cb ->
            var predicate = cb.conjunction()
            if (!symbol.isNullOrBlank()) {
                predicate = cb.and(
                    predicate,
                    cb.like(
                        cb.lower(root.get("symbol")),
                        "${symbol.lowercase()}%"
                    )
                )
            }

            if (!currency.isNullOrBlank()) {
                predicate = cb.and(
                    predicate,
                    cb.equal(root.get<Currency>("currency").get<String>("code"), currency)
                )
            }

            if (!exchange.isNullOrBlank()) {
                val listedAssetRoot = cb.treat(root, ListedAsset::class.java)
                predicate = cb.and(
                    predicate,
                    cb.equal(root.get<Exchange>("exchange").get<String>("acronym"), exchange)
                )
            }

            if (!company.isNullOrBlank()) {
                val stockRoot = cb.treat(root, Stock::class.java)
                predicate = cb.and(
                    predicate,
                    cb.like(
                        cb.lower(stockRoot.get<Company>("company").get("name")),
                        company.lowercase() + "%"
                    )
                )
            }

            predicate
        }
    }
}