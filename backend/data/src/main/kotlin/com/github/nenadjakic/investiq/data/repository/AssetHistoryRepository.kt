package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.history.AssetHistory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AssetHistoryRepository: JpaRepository<AssetHistory, UUID> {

    fun findTopByAsset_SymbolOrderByValidDateDesc(
        symbol: String,
    ): AssetHistory?
}