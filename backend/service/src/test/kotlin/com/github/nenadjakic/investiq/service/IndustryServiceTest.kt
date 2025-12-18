package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.data.entity.core.Industry
import com.github.nenadjakic.investiq.data.entity.core.Sector
import com.github.nenadjakic.investiq.data.repository.IndustryRepository
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
class IndustryServiceTest {

    @Mock
    private lateinit var industryRepository: IndustryRepository

    private lateinit var industryService: IndustryService

    @BeforeEach
    fun setUp() {
        industryService = IndustryService(industryRepository)
    }

    @Test
    fun `findAll should return empty list when no industries exist`() {
        `when`(industryRepository.findAll()).thenReturn(emptyList())

        val result = industryService.findAll()

        assertTrue(result.isEmpty())
        verify(industryRepository).findAll()
    }

    @Test
    fun `findAll should return list of IndustryResponse when industries exist`() {
        val sector = Sector(id = UUID.randomUUID(), name = "Technology")
        val industry1 = Industry(
            id = UUID.randomUUID(),
            name = "Semiconductors",
            sector = sector
        )
        val industry2 = Industry(
            id = UUID.randomUUID(),
            name = "Software",
            sector = sector
        )

        `when`(industryRepository.findAll()).thenReturn(listOf(industry1, industry2))

        val result = industryService.findAll()

        assertEquals(2, result.size)
        assertEquals("Semiconductors", result[0].name)
        assertEquals("Technology", result[0].sector.name)
        assertEquals("Software", result[1].name)
        assertEquals("Technology", result[1].sector.name)
        verify(industryRepository).findAll()
    }

    @Test
    fun `findAll should return industries from different sectors`() {
        val techSector = Sector(id = UUID.randomUUID(), name = "Technology")
        val healthSector = Sector(id = UUID.randomUUID(), name = "Healthcare")

        val semiconductors = Industry(id = UUID.randomUUID(), name = "Semiconductors", sector = techSector)
        val biotech = Industry(id = UUID.randomUUID(), name = "Biotechnology", sector = healthSector)

        `when`(industryRepository.findAll()).thenReturn(listOf(semiconductors, biotech))

        val result = industryService.findAll()

        assertEquals(2, result.size)
        assertEquals("Technology", result[0].sector.name)
        assertEquals("Healthcare", result[1].sector.name)
    }
}

