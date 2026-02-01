package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.common.dto.asset.AssetAddRequest
import com.github.nenadjakic.investiq.common.dto.asset.AssetResponse
import com.github.nenadjakic.investiq.common.dto.asset.toAssetResponse
import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.AssetAlias
import com.github.nenadjakic.investiq.data.entity.asset.Etf
import com.github.nenadjakic.investiq.data.entity.asset.Index
import com.github.nenadjakic.investiq.data.entity.asset.ListedAsset
import com.github.nenadjakic.investiq.data.entity.asset.Stock
import com.github.nenadjakic.investiq.data.entity.core.Company
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Exchange
import com.github.nenadjakic.investiq.data.enum.AssetType
import com.github.nenadjakic.investiq.data.repository.AssetRepository
import com.github.nenadjakic.investiq.data.repository.CompanyRepository
import com.github.nenadjakic.investiq.data.repository.CurrencyRepository
import com.github.nenadjakic.investiq.data.repository.ExchangeRepository
import com.github.nenadjakic.investiq.data.repository.IndexRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class AssetService(
    private val assetRepository: AssetRepository,
    private val companyRepository: CompanyRepository,
    private val exchangeRepository: ExchangeRepository,
    private val currencyRepository: CurrencyRepository,
    private val indexRepository: IndexRepository
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
        pageable: Pageable
    ): Page<AssetResponse> {
        val spec = getSpecification(symbol, currency, exchange, company)

        return assetRepository.findAll(spec, pageable).map { it.toAssetResponse() }
    }

    fun findById(assetId: UUID): AssetResponse? =
        assetRepository.findByIdOrNull(assetId)?.toAssetResponse()

    @Transactional
    fun findAll(): List<AssetResponse> =
        assetRepository.findAll().map { it.toAssetResponse() }

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
                cb.treat(root, ListedAsset::class.java)
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

    fun create(request: AssetAddRequest): AssetResponse {
        val asset = when (request.assetType) {
            AssetType.STOCK -> createStock(request)
            AssetType.ETF -> createEtf(request)
            AssetType.INDEX -> createIndex(request)
            else -> {
                throw IllegalArgumentException("Unsupported asset type: ${request.assetType}")
            }
        }

        return assetRepository.save(asset).toAssetResponse()
    }

    private fun createStock(request: AssetAddRequest): Stock {
        return Stock().apply {
            applyBaseProperties(request)
            company = companyRepository.getReferenceById(request.companyId!!)
            exchange = exchangeRepository.getReferenceById(request.exchangeId!!)
        }
    }

    private fun createEtf(request: AssetAddRequest): Etf {
        return Etf().apply {
            applyBaseProperties(request)
            fundManager = request.fundManager!!
            assetClass = request.assetClass!!
            trackedIndex = request.trackedIndexId?.let { indexRepository.getReferenceById(it) }
        }
    }

    private fun createIndex(request: AssetAddRequest): Index {
        return Index().apply {
            applyBaseProperties(request)
        }
    }

    private fun <T : Asset> T.applyBaseProperties(request: AssetAddRequest) {
        assetType = request.assetType!!
        symbol = request.symbol!!
        name = request.name!!
        currency = currencyRepository.getReferenceById(request.currencyCode!!)

        request.aliases?.forEach { (platform, aliasValue) ->
            aliases.add(
                AssetAlias(
                    asset = this,
                    platform = platform,
                    externalSymbol = aliasValue,
                    id = null
                )
            )
        }
    }
}