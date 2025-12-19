package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.data.entity.core.Company
import com.github.nenadjakic.investiq.data.entity.core.Country
import com.github.nenadjakic.investiq.data.entity.core.Industry
import com.github.nenadjakic.investiq.data.entity.core.Sector
import com.github.nenadjakic.investiq.data.repository.CompanyRepository
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
class CompanyServiceTest {

    @Mock
    private lateinit var companyRepository: CompanyRepository

    private lateinit var companyService: CompanyService

    @BeforeEach
    fun setUp() {
        companyService = CompanyService(companyRepository)
    }

    @Test
    fun `findAll should return empty list when no companies exist`() {
        `when`(companyRepository.findAll()).thenReturn(emptyList())

        val result = companyService.findAll()

        assertTrue(result.isEmpty())
        verify(companyRepository).findAll()
    }

    @Test
    fun `findAll should return list of CompanyResponse when companies exist`() {
        val usa = Country(iso2Code = "US", name = "United States")
        val sector = Sector(id = UUID.randomUUID(), name = "Technology")
        val industry = Industry(id = UUID.randomUUID(), name = "Semiconductors", sector = sector)

        val company = Company(
            id = UUID.randomUUID(),
            name = "NVIDIA Corporation",
            country = usa,
            industry = industry
        )

        `when`(companyRepository.findAll()).thenReturn(listOf(company))

        val result = companyService.findAll()

        assertEquals(1, result.size)
        assertEquals("NVIDIA Corporation", result[0].name)
        assertEquals("US", result[0].country.code)
        assertEquals("United States", result[0].country.name)
        assertEquals("Semiconductors", result[0].industry.name)
        assertEquals("Technology", result[0].industry.sector.name)
        verify(companyRepository).findAll()
    }

    @Test
    fun `findAll should return multiple companies correctly`() {
        val usa = Country(iso2Code = "US", name = "United States")
        val germany = Country(iso2Code = "DE", name = "Germany")
        val techSector = Sector(id = UUID.randomUUID(), name = "Technology")
        val autoSector = Sector(id = UUID.randomUUID(), name = "Consumer Cyclical")
        val semiconductorIndustry = Industry(id = UUID.randomUUID(), name = "Semiconductors", sector = techSector)
        val autoIndustry = Industry(id = UUID.randomUUID(), name = "Auto Manufacturers", sector = autoSector)

        val nvidia = Company(
            id = UUID.randomUUID(),
            name = "NVIDIA Corporation",
            country = usa,
            industry = semiconductorIndustry
        )
        val bmw = Company(
            id = UUID.randomUUID(),
            name = "BMW AG",
            country = germany,
            industry = autoIndustry
        )

        `when`(companyRepository.findAll()).thenReturn(listOf(nvidia, bmw))

        val result = companyService.findAll()

        assertEquals(2, result.size)
        assertEquals("NVIDIA Corporation", result[0].name)
        assertEquals("US", result[0].country.code)
        assertEquals("BMW AG", result[1].name)
        assertEquals("DE", result[1].country.code)
    }
}

