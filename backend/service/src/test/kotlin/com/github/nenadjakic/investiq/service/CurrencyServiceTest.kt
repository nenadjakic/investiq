package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.repository.CurrencyRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CurrencyServiceTest {

    @Mock
    private lateinit var currencyRepository: CurrencyRepository

    private lateinit var currencyService: CurrencyService

    @BeforeEach
    fun setUp() {
        currencyService = CurrencyService(currencyRepository)
    }

    @Test
    fun `findAll should return empty list when no currencies exist`() {
        `when`(currencyRepository.findAll()).thenReturn(emptyList())

        val result = currencyService.findAll()

        assertTrue(result.isEmpty())
        verify(currencyRepository).findAll()
    }

    @Test
    fun `findAll should return list of CurrencyResponse when currencies exist`() {
        val eur = Currency().apply {
            code = "EUR"
            name = "Euro"
        }
        val usd = Currency().apply {
            code = "USD"
            name = "US Dollar"
        }

        `when`(currencyRepository.findAll()).thenReturn(listOf(eur, usd))

        val result = currencyService.findAll()

        assertEquals(2, result.size)
        verify(currencyRepository).findAll()
    }

    @Test
    fun `findAll should return single currency correctly`() {
        val gbp = Currency().apply {
            code = "GBP"
            name = "British Pound"
        }

        `when`(currencyRepository.findAll()).thenReturn(listOf(gbp))

        val result = currencyService.findAll()

        assertEquals(1, result.size)
    }
}

