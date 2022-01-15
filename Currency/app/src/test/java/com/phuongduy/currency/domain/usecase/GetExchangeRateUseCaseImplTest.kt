package com.phuongduy.currency.domain.usecase

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.coroutine.resource.Resource.Companion.success
import com.phuongduy.currency.coroutine.resource.Resource.Companion.error
import com.phuongduy.currency.domain.CurrencyRepository
import com.phuongduy.currency.domain.entity.ExchangeRate
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as whenever
import org.mockito.Mockito.verify
import java.sql.SQLException

class GetExchangeRateUseCaseImplTest {
    private val currencyRepository = mock(CurrencyRepository::class.java)

    private val useCaseImpl = GetExchangeRateUseCaseImpl(currencyRepository)

    @Test
    fun `execute, get data from repository successfully`() {
        runBlocking {
            val usdToJpyExchangeRate = ExchangeRate("JPY", "USD", 114.3)
            whenever(currencyRepository.getExchangeRate("JPY"))
                .thenReturn(success(usdToJpyExchangeRate))

            val result = (useCaseImpl.execute("JPY") as Resource.Success<ExchangeRate>).data

            assertEquals(usdToJpyExchangeRate, result)

            verify(currencyRepository).getExchangeRate("JPY")
        }
    }

    @Test
    fun `execute, fail to get data from repository`() {
        runBlocking {
            val error = SQLException()
            whenever(currencyRepository.getExchangeRate("JPY"))
                .thenReturn(error(error))

            val result = (useCaseImpl.execute("JPY") as Resource.Error<ExchangeRate>).error

            assertEquals(error, result)

            verify(currencyRepository).getExchangeRate("JPY")
        }
    }
}