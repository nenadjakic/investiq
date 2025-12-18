package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.data.entity.core.Industry
import com.github.nenadjakic.investiq.data.entity.core.Sector
import com.github.nenadjakic.investiq.data.repository.SectorRepository
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
class SectorServiceTest {

    @Mock
    private lateinit var sectorRepository: SectorRepository

    private lateinit var sectorService: SectorService

    @BeforeEach
    fun setUp() {
        sectorService = SectorService(sectorRepository)
    }

    @Test
    fun `findAll should return empty list when no sectors exist`() {
        `when`(sectorRepository.findAll()).thenReturn(emptyList())

        val result = sectorService.findAll()

        assertTrue(result.isEmpty())
        verify(sectorRepository).findAll()
    }

    @Test
    fun `findAll should return list of SectorResponse when sectors exist`() {
        val sectorId = UUID.randomUUID()
        val industryId = UUID.randomUUID()
        val sector = Sector(
            id = sectorId,
            name = "Technology"
        )
        val industry = Industry(
            id = industryId,
            name = "Semiconductors",
            sector = sector
        )
        sector.industries.add(industry)

        `when`(sectorRepository.findAll()).thenReturn(listOf(sector))

        val result = sectorService.findAll()

        assertEquals(1, result.size)
        assertEquals(sectorId, result[0].id)
        assertEquals("Technology", result[0].name)
        assertEquals(1, result[0].industries.size)
        assertEquals(industryId, result[0].industries[0].id)
        assertEquals("Semiconductors", result[0].industries[0].name)
        verify(sectorRepository).findAll()
    }

    @Test
    fun `findAll should return sector with empty industries list`() {
        val sectorId = UUID.randomUUID()
        val sector = Sector(
            id = sectorId,
            name = "Energy"
        )

        `when`(sectorRepository.findAll()).thenReturn(listOf(sector))

        val result = sectorService.findAll()

        assertEquals(1, result.size)
        assertEquals("Energy", result[0].name)
        assertTrue(result[0].industries.isEmpty())
    }

    @Test
    fun `findAll should return multiple sectors with multiple industries`() {
        val sector1Id = UUID.randomUUID()
        val sector2Id = UUID.randomUUID()
        val industry1Id = UUID.randomUUID()
        val industry2Id = UUID.randomUUID()
        val industry3Id = UUID.randomUUID()

        val sector1 = Sector(id = sector1Id, name = "Technology")
        val sector2 = Sector(id = sector2Id, name = "Healthcare")

        sector1.industries.add(Industry(id = industry1Id, name = "Semiconductors", sector = sector1))
        sector1.industries.add(Industry(id = industry2Id, name = "Software", sector = sector1))
        sector2.industries.add(Industry(id = industry3Id, name = "Biotechnology", sector = sector2))

        `when`(sectorRepository.findAll()).thenReturn(listOf(sector1, sector2))

        val result = sectorService.findAll()

        assertEquals(2, result.size)
        assertEquals(2, result[0].industries.size)
        assertEquals(1, result[1].industries.size)
    }
}

