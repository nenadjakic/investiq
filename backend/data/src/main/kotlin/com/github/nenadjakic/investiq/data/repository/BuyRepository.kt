package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.transaction.Buy
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime
import java.util.UUID

interface BuyRepository: JpaRepository<Buy, UUID> {
    fun findAllByAssetAndDateLessThanEqual(asset: Asset, toDate: OffsetDateTime): List<Buy>
}