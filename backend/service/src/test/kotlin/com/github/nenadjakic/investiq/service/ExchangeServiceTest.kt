package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.data.entity.core.Country
import com.github.nenadjakic.investiq.data.entity.core.Exchange
import com.github.nenadjakic.investiq.data.repository.ExchangeRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ExchangeServiceTest {

    @Mock
    private lateinit var exchangeRepository: ExchangeRepository

    private lateinit var exchangeService: ExchangeService

    @BeforeEach
    fun setUp() {
        exchangeService = ExchangeService(exchangeRepository)
    }

    @Test
    fun `findAll should return empty list when no exchanges exist`() {
        `when`(exchangeRepository.findAll()).thenReturn(emptyList())

        val result = exchangeService.findAll()

        assertTrue(result.isEmpty())
        verify(exchangeRepository).findAll()
    }

    @Test
    fun `findAll should return list of ExchangeResponse when exchanges exist`() {
        val germany = Country(iso2Code = "DE", name = "Germany")
        val usa = Country(iso2Code = "US", name = "United States")

        val fse = Exchange(
            id = UUID.randomUUID(),
            mic = "XFRA",
            acronym = "FSE",
            name = "Frankfurt Stock Exchange",
            country = germany
        )
        val nyse = Exchange(
            id = UUID.randomUUID(),
            mic = "XNYS",
            acronym = "NYSE",
            name = "New York Stock Exchange",
            country = usa
        )

        `when`(exchangeRepository.findAll()).thenReturn(listOf(fse, nyse))

        val result = exchangeService.findAll()

        assertEquals(2, result.size)

        assertEquals("XFRA", result[0].mic)
        assertEquals("FSE", result[0].symbol)
        assertEquals("Frankfurt Stock Exchange", result[0].name)
        assertEquals("DE", result[0].country.code)
        assertEquals("Germany", result[0].country.name)

        assertEquals("XNYS", result[1].mic)
        assertEquals("NYSE", result[1].symbol)
        assertEquals("New York Stock Exchange", result[1].name)
        assertEquals("US", result[1].country.code)

        verify(exchangeRepository).findAll()
    }

    @Test
    fun `findAll should return exchange without acronym`() {
        val uk = Country(iso2Code = "GB", name = "United Kingdom")
        val lse = Exchange(
            id = UUID.randomUUID(),
            mic = "XLON",
            acronym = null,
            name = "London Stock Exchange",
            country = uk
        )

        `when`(exchangeRepository.findAll()).thenReturn(listOf(lse))

        val result = exchangeService.findAll()

        assertEquals(1, result.size)
        assertEquals("XLON", result[0].mic)
        assertNull(result[0].symbol)
        assertEquals("London Stock Exchange", result[0].name)
    }
}

