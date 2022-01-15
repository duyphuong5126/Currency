package com.phuongduy.currency.domain.usecase

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.domain.CurrencyRepository
import com.phuongduy.currency.domain.entity.ExchangeRate
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as whenever
import org.mockito.Mockito.verify
import java.sql.SQLException

class GetExchangeRateListUseCaseImplTest {
    private val currencyRepository = mock(CurrencyRepository::class.java)

    private val useCaseImpl = GetExchangeRateListUseCaseImpl(currencyRepository)

    @Test
    fun `execute, get data from repository successfully`() {
        runBlocking {
            val usdToJpyExchangeRateList = listOf(ExchangeRate("AUD", "USD", 1.3))
            whenever(currencyRepository.getExchangeRateList(0, 25, "JPY"))
                .thenReturn(Resource.success(usdToJpyExchangeRateList))

            val result =
                (useCaseImpl.execute(0, 25, "JPY") as Resource.Success<List<ExchangeRate>>).data

            assertEquals(usdToJpyExchangeRateList, result)

            verify(currencyRepository).getExchangeRateList(0, 25, "JPY")
        }
    }

    @Test
    fun `execute, fail to get data from repository`() {
        runBlocking {
            val error = SQLException()
            whenever(currencyRepository.getExchangeRateList(0, 25, "JPY"))
                .thenReturn(Resource.error(error))

            val result =
                (useCaseImpl.execute(0, 25, "JPY") as Resource.Error<List<ExchangeRate>>).error

            assertEquals(error, result)

            verify(currencyRepository).getExchangeRateList(0, 25, "JPY")
        }
    }
}