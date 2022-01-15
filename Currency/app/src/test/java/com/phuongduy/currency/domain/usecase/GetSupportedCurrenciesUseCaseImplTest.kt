package com.phuongduy.currency.domain.usecase

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.domain.CurrencyRepository
import com.phuongduy.currency.domain.entity.Currency
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as whenever
import org.mockito.Mockito.verify
import java.net.SocketTimeoutException

class GetSupportedCurrenciesUseCaseImplTest {

    private val currencyRepository = mock(CurrencyRepository::class.java)

    private val useCaseImpl = GetSupportedCurrenciesUseCaseImpl(currencyRepository)

    @Test
    fun `execute, repository returns data`() {
        runBlocking {
            val supportedCurrencyList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United State Dollar"))
                add(Currency("JPY", "Japanese Yen"))
                add(Currency("VND", "Vietnamese Dong"))
            }
            whenever(currencyRepository.getSupportedCurrencies())
                .thenReturn(Resource.Success(supportedCurrencyList))

            val result = useCaseImpl.execute()

            assertEquals(supportedCurrencyList, (result as Resource.Success<List<Currency>>).data)

            verify(currencyRepository).getSupportedCurrencies()
        }
    }

    @Test
    fun `execute, repository returns an error`() {
        runBlocking {
            val error = SocketTimeoutException("")
            whenever(currencyRepository.getSupportedCurrencies())
                .thenReturn(Resource.Error(error))

            val result = useCaseImpl.execute()

            assertEquals(error, (result as Resource.Error<List<Currency>>).error)

            verify(currencyRepository).getSupportedCurrencies()
        }
    }
}