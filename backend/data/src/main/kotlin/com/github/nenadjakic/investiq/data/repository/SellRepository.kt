package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.transaction.Sell
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime
import java.util.UUID

interface SellRepository: JpaRepository<Sell, UUID> {
    fun findAllByAssetAndDateLessThanEqual(asset: Asset, toDate: OffsetDateTime): List<Sell>
}