package com.github.nenadjakic.investiq.service.importer

import com.github.nenadjakic.investiq.data.entity.asset.AssetAlias
import com.github.nenadjakic.investiq.data.entity.asset.Stock
import com.github.nenadjakic.investiq.data.entity.core.Currency
import com.github.nenadjakic.investiq.data.entity.core.Tag
import com.github.nenadjakic.investiq.data.repository.AssetAliasRepository
import com.github.nenadjakic.investiq.data.repository.CurrencyRepository
import com.github.nenadjakic.investiq.data.repository.StagingTransactionRepository
import com.github.nenadjakic.investiq.data.repository.TagRepository
import com.github.nenadjakic.investiq.data.enum.Platform
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class Trading212ImporterServiceTest {

    @Mock
    private lateinit var assetAliasRepository: AssetAliasRepository

    @Mock
    private lateinit var currencyRepository: CurrencyRepository

    @Mock
    private lateinit var stagingTransactionRepository: StagingTransactionRepository

    @Mock
    private lateinit var tagRepository: TagRepository

    private lateinit var service: Trading212ImporterService

    @BeforeEach
    fun setUp() {
        service = Trading212ImporterService(assetAliasRepository, currencyRepository, stagingTransactionRepository, tagRepository)
    }

    @Test
    fun `mapRecord should parse a valid CSV record`() {
        val csv = "Action,Time,ISIN,Ticker,Name,Notes,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Currency (Result),Total,Currency (Total),Withholding tax,Currency (Withholding tax)\n" +
                "Market buy,2023-01-02 15:04:05,GB00B03MLX29,AAPL,Apple,,1,2,12.34,USD,1.0,USD,24.68,USD,0,"

        val input = ByteArrayInputStream(csv.toByteArray(StandardCharsets.UTF_8))
        val format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).setIgnoreSurroundingSpaces(true).get()

        CSVParser.parse(input, StandardCharsets.UTF_8, format).use { parser ->
            val record = parser.records.first()
            val method = Trading212ImporterService::class.java.getDeclaredMethod("mapRecord", org.apache.commons.csv.CSVRecord::class.java)
            method.isAccessible = true
            val mapped = method.invoke(service, record) as com.github.nenadjakic.investiq.service.model.importer.Trading212Trade

            assertEquals(2.0, mapped.numberOfShares)
            assertEquals(12.34, mapped.pricePerShare)
            assertEquals("AAPL", mapped.ticker)
            assertEquals(LocalDateTime.of(2023,1,2,15,4,5), mapped.time)
        }
    }

    @Test
    fun `addToStaging should load lookups and save staging transactions`() {
        // prepare repositories to return lookups
        whenever(assetAliasRepository.findAllByPlatform(Platform.TRADING212)).thenReturn(listOf(AssetAlias(null, Platform.TRADING212, "AAPL", Stock())))
        val currency = Currency()
        currency.code = "USD"
        currency.name = "US Dollar"
        whenever(currencyRepository.findAll()).thenReturn(listOf(currency))
        whenever(tagRepository.findAll()).thenReturn(listOf(Tag(name = "tag1")))

        // prepare a successful rowResult list via invoking mapRecord
        val csv = "Action,Time,ISIN,Ticker,Name,Notes,ID,No. of shares,Price / share,Currency (Price / share),Exchange rate,Currency (Result),Total,Currency (Total),Withholding tax,Currency (Withholding tax)\n" +
                "Market buy,2023-01-02 15:04:05,GB00B03MLX29,AAPL,Apple,,1,2,12.34,USD,1.0,USD,24.68,USD,0,\n"
        val input = ByteArrayInputStream(csv.toByteArray(StandardCharsets.UTF_8))
        val format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).setIgnoreSurroundingSpaces(true).get()
        val rowResults = mutableListOf<com.github.nenadjakic.investiq.common.dto.RowResult<com.github.nenadjakic.investiq.service.model.importer.Trading212Trade>>()

        CSVParser.parse(input, StandardCharsets.UTF_8, format).use { parser ->
            val record = parser.records.first()
            val method = Trading212ImporterService::class.java.getDeclaredMethod("mapRecord", org.apache.commons.csv.CSVRecord::class.java)
            method.isAccessible = true
            val mapped = method.invoke(service, record) as com.github.nenadjakic.investiq.service.model.importer.Trading212Trade
            rowResults.add(com.github.nenadjakic.investiq.common.dto.RowResult(1, com.github.nenadjakic.investiq.common.dto.RowStatus.SUCCESS, mapped))
        }

        val addToStaging = Trading212ImporterService::class.java.getDeclaredMethod("addToStaging", MutableList::class.java)
        addToStaging.isAccessible = true
        addToStaging.invoke(service, rowResults)

        verify(stagingTransactionRepository).saveAll(anyList())
    }
}
