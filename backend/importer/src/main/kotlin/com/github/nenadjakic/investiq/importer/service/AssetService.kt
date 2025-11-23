package com.github.nenadjakic.investiq.importer.service

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Exchange
import com.github.nenadjakic.investiq.data.repository.AssetRepository
import com.github.nenadjakic.investiq.importer.model.AssetResponse
import com.github.nenadjakic.investiq.importer.model.toAssetResponse
import jakarta.transaction.Transactional
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AssetService(
    private val assetRepository: AssetRepository
) {
    @Transactional
    fun findAll(symbol: String, currency: String?, exchange: String?): List<AssetResponse> {
        val spec = Specification<Asset> { root, query, cb ->
            var predicate = cb.like(
                cb.lower(root.get("symbol")),
                "${symbol.lowercase()}%"
            )

            if (!currency.isNullOrBlank()) {
                predicate = cb.and(
                    predicate,
                    cb.equal(root.get<Currency>("currency").get<String>("code"), currency)
                )
            }

            if (!exchange.isNullOrBlank()) {
                predicate = cb.and(
                    predicate,
                    cb.equal(root.get<Exchange>("exchange").get<String>("acronym"), exchange)
                )
            }

            predicate
        }

        return assetRepository.findAll(spec).map { it.toAssetResponse() }
    }

    fun findById(assetId: UUID) =
        assetRepository.findById(assetId).map { it.toAssetResponse() }
}