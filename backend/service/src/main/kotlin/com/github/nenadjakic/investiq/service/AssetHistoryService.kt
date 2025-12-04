package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.data.repository.AssetHistoryRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AssetHistoryService(
    private val assetHistoryRepository: AssetHistoryRepository
) {

    fun getLatestValidDate(symbol: String): LocalDate? {
        val history = assetHistoryRepository
            .findTopByAsset_SymbolOrderByValidDateDesc(symbol)
        return history?.validDate
    }
}