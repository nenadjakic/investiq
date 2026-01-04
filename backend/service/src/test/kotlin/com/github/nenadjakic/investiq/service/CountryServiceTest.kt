package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.data.entity.core.Continent
import com.github.nenadjakic.investiq.data.entity.core.Country
import com.github.nenadjakic.investiq.data.repository.CountryRepository
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
class CountryServiceTest {

    @Mock
    private lateinit var countryRepository: CountryRepository

    private lateinit var countryService: CountryService

    @BeforeEach
    fun setUp() {
        countryService = CountryService(countryRepository)
    }

    @Test
    fun `findAll should return empty list when no countries exist`() {
        `when`(countryRepository.findAll()).thenReturn(emptyList())

        val result = countryService.findAll()

        assertTrue(result.isEmpty())
        verify(countryRepository).findAll()
    }

    @Test
    fun `findAll should return list of CountryResponse when countries exist`() {
        val germany = Country(iso2Code = "DE", name = "Germany", Continent(UUID.randomUUID(), "Europe"))
        val usa = Country(iso2Code = "US", name = "United States", Continent(UUID.randomUUID(), "North America"))

        `when`(countryRepository.findAll()).thenReturn(listOf(germany, usa))

        val result = countryService.findAll()

        assertEquals(2, result.size)
        assertEquals("DE", result[0].code)
        assertEquals("Germany", result[0].name)
        assertEquals("US", result[1].code)
        assertEquals("United States", result[1].name)
        verify(countryRepository).findAll()
    }

    @Test
    fun `findAll should return single country correctly`() {
        val france = Country(iso2Code = "FR", name = "France", Continent(UUID.randomUUID(), "Europe"))

        `when`(countryRepository.findAll()).thenReturn(listOf(france))

        val result = countryService.findAll()

        assertEquals(1, result.size)
        assertEquals("FR", result[0].code)
        assertEquals("France", result[0].name)
    }
}

