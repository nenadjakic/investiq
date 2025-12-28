package com.github.nenadjakic.investiq.service

import com.github.nenadjakic.investiq.data.entity.Action
import com.github.nenadjakic.investiq.data.entity.asset.Asset
import com.github.nenadjakic.investiq.data.entity.asset.Stock
import com.github.nenadjakic.investiq.data.entity.transaction.Buy
import com.github.nenadjakic.investiq.data.entity.transaction.Transaction
import com.github.nenadjakic.investiq.data.repository.ActionRepository
import com.github.nenadjakic.investiq.data.repository.BuyRepository
import com.github.nenadjakic.investiq.data.repository.SellRepository
import com.github.nenadjakic.investiq.data.repository.TransactionRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class ActionServiceTest {

    @Mock
    private lateinit var actionRepository: ActionRepository

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @Mock
    private lateinit var buyRepository: BuyRepository

    @Mock
    private lateinit var sellRepository: SellRepository

    private lateinit var actionService: ActionService

    @BeforeEach
    fun setUp() {
        actionService = ActionService(actionRepository, transactionRepository, buyRepository, sellRepository)
    }

    @Test
    fun `parseFactor should compute correct factor for 2_FOR_1`() {
        val method = ActionService::class.java.getDeclaredMethod("parseFactor", String::class.java)
        method.isAccessible = true
        val result = method.invoke(actionService, "SPLIT_2_FOR_1") as BigDecimal
        assertEquals(0, result.compareTo(BigDecimal("2")))
    }

    @Test
    fun `parseFactor should throw for invalid format`() {
        val method = ActionService::class.java.getDeclaredMethod("parseFactor", String::class.java)
        method.isAccessible = true
        val ex = assertThrows(InvocationTargetException::class.java) {
            method.invoke(actionService, "INVALID_FORMAT")
        }
        assertTrue(ex.cause is IllegalArgumentException)
    }

    @Test
    fun `applyFactor should adjust buy quantity and price`() {
        val method = ActionService::class.java.getDeclaredMethod("applyFactor", Transaction::class.java, java.math.BigDecimal::class.java)
        method.isAccessible = true

        val buy = Buy()
        buy.quantity = BigDecimal("10")
        buy.price = BigDecimal("100")

        method.invoke(actionService, buy, BigDecimal(2))

        assertEquals(0, buy.quantity.compareTo(BigDecimal("20")))
        assertEquals(0, buy.price.compareTo(BigDecimal("50")))
    }

    @Test
    fun `execute should process actions and save transactions`() {
        val action = Action()
        action.executed = false
        action.date = LocalDate.now()
        val buy = Buy()
        buy.quantity = BigDecimal("5")
        buy.price = BigDecimal("10")

        whenever(actionRepository.findAllByExecutedFalseAndDateLessThanEqual(any()))
            .thenReturn(listOf(action))

        whenever(buyRepository.findAllByAssetAndDateLessThanEqual(any(), any()))
            .thenReturn(listOf(buy))

        whenever(sellRepository.findAllByAssetAndDateLessThanEqual(any(), any()))
            .thenReturn(emptyList())

        action.rule = "SPLIT_2_FOR_1"
        action.asset = Stock()

        actionService.execute()

        assertTrue(action.executed)
        verify(transactionRepository).saveAll(anyList())
        verify(actionRepository).saveAll(anyList())
    }
}
