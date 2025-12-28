package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.data.entity.asset.Stock
import com.github.nenadjakic.investiq.data.entity.history.AssetHistory
import com.github.nenadjakic.investiq.data.repository.AssetHistoryRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class AssetHistoryServiceTest {

    @Mock
    private lateinit var assetHistoryRepository: AssetHistoryRepository

    private lateinit var service: AssetHistoryService

    @BeforeEach
    fun setUp() {
        service = AssetHistoryService(assetHistoryRepository)
    }

    @Test
    fun `getLatestValidDate should return date when history exists`() {
        // create minimal Stock asset and AssetHistory with required fields
        val stock = Stock()
        stock.symbol = "SYM"
        stock.name = "Sym Inc"
        stock.currency = com.github.nenadjakic.investiq.data.entity.core.Currency()
        val date = LocalDate.of(2024, 1, 2)
        val history = AssetHistory(null, stock, date, null, null, null, null, BigDecimal("10.0"), null)

        `when`(assetHistoryRepository.findTopByAsset_SymbolOrderByValidDateDesc(anyString())).thenReturn(history)

        val result = service.getLatestValidDate("SYM")

        assertEquals(date, result)
    }

    @Test
    fun `getLatestValidDate should return null when no history`() {
        `when`(assetHistoryRepository.findTopByAsset_SymbolOrderByValidDateDesc(anyString())).thenReturn(null)

        val result = service.getLatestValidDate("SYM")

        assertNull(result)
    }
}
