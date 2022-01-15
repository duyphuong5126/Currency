package com.phuongduy.currency.data

import com.google.gson.JsonParseException
import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.coroutine.resource.Resource.Companion.success
import com.phuongduy.currency.coroutine.resource.Resource.Companion.error
import com.phuongduy.currency.data.local.CurrencyLocalDataSource
import com.phuongduy.currency.data.remote.CurrencyRemoteDataSource
import com.phuongduy.currency.domain.entity.Currency
import com.phuongduy.currency.domain.entity.ExchangeRate
import com.phuongduy.currency.safelyAnyList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.never
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when` as whenever
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CurrencyRepositoryImplTest {
    private val localDataSource = mock(CurrencyLocalDataSource::class.java)
    private val remoteDataSource = mock(CurrencyRemoteDataSource::class.java)

    private val repositoryImpl = CurrencyRepositoryImpl(localDataSource, remoteDataSource)

    @Test
    fun `getSupportedCurrencies, remote data source returns data`() {
        runBlocking {
            val remoteList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United States Dollar"))
                add(Currency("JPY", "Japanese Yen"))
            }
            whenever(remoteDataSource.getSupportedCurrencies())
                .thenReturn(success(remoteList))

            val resultList =
                (repositoryImpl.getSupportedCurrencies() as Resource.Success<List<Currency>>).data

            assertEquals(remoteList, resultList)

            verify(remoteDataSource).getSupportedCurrencies()
            verify(localDataSource).storeSupportedCurrencyList(remoteList)
            verify(localDataSource, never()).getCurrencyList()
        }
    }

    @Test
    fun `getSupportedCurrencies, remote data source returns an empty list`() {
        runBlocking {
            val remoteList = arrayListOf<Currency>()
            whenever(remoteDataSource.getSupportedCurrencies())
                .thenReturn(success(remoteList))

            val resultList =
                (repositoryImpl.getSupportedCurrencies() as Resource.Success<List<Currency>>).data

            assertEquals(remoteList, resultList)

            verify(remoteDataSource).getSupportedCurrencies()
            verify(localDataSource, never()).storeSupportedCurrencyList(remoteList)
            verify(localDataSource, never()).getCurrencyList()
        }
    }

    @Test
    fun `getSupportedCurrencies, remote data source returns a network error`() {
        runBlocking {
            whenever(remoteDataSource.getSupportedCurrencies())
                .thenReturn(error(UnknownHostException()))

            val localList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United States Dollar"))
                add(Currency("JPY", "Japanese Yen"))
            }
            whenever(localDataSource.getCurrencyList()).thenReturn(success(localList))

            val resultList =
                (repositoryImpl.getSupportedCurrencies() as Resource.Success<List<Currency>>).data

            assertEquals(localList, resultList)

            verify(remoteDataSource).getSupportedCurrencies()
            verify(localDataSource, never()).storeSupportedCurrencyList(safelyAnyList())
            verify(localDataSource).getCurrencyList()
        }
    }

    @Test
    fun `getSupportedCurrencies, remote data source returns a timeout error`() {
        runBlocking {
            whenever(remoteDataSource.getSupportedCurrencies())
                .thenReturn(error(SocketTimeoutException()))

            val localList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United States Dollar"))
                add(Currency("JPY", "Japanese Yen"))
            }
            whenever(localDataSource.getCurrencyList()).thenReturn(success(localList))

            val resultList =
                (repositoryImpl.getSupportedCurrencies() as Resource.Success<List<Currency>>).data

            assertEquals(localList, resultList)

            verify(remoteDataSource).getSupportedCurrencies()
            verify(localDataSource, never()).storeSupportedCurrencyList(safelyAnyList())
            verify(localDataSource).getCurrencyList()
        }
    }

    @Test
    fun `getSupportedCurrencies, remote data source returns a data parsing error`() {
        runBlocking {
            val parsingError = JsonParseException("")
            whenever(remoteDataSource.getSupportedCurrencies()).thenReturn(error(parsingError))

            val result =
                (repositoryImpl.getSupportedCurrencies() as Resource.Error<List<Currency>>).error

            assertEquals(parsingError, result)

            verify(remoteDataSource).getSupportedCurrencies()
            verify(localDataSource, never()).storeSupportedCurrencyList(safelyAnyList())
            verify(localDataSource, never()).getCurrencyList()
        }
    }

    @Test
    fun `getExchangeRateList, supported currency list is empty`() {
        runBlocking {
            val localCurrencyList = arrayListOf<Currency>()
            whenever(localDataSource.getCurrencyList())
                .thenReturn(success(localCurrencyList))

            val excludedCurrencyCode = "JPY"
            val localExchangeRateList = arrayListOf<ExchangeRate>()
            whenever(localDataSource.getExchangeRateList(0, 20, excludedCurrencyCode))
                .thenReturn(success(localExchangeRateList))

            val resultList = (repositoryImpl.getExchangeRateList(0, 20, excludedCurrencyCode)
                    as Resource.Success<List<ExchangeRate>>).data

            assertTrue(resultList.isEmpty())

            verify(localDataSource).getCurrencyList()
            verify(remoteDataSource, never()).getExchangeRateList(safelyAnyList())
            verify(localDataSource, never()).storeExchangeRateList(safelyAnyList())
            verify(localDataSource).getExchangeRateList(0, 20, excludedCurrencyCode)
        }
    }

    @Test
    fun `getExchangeRateList, remote exchange rate list is empty`() {
        runBlocking {
            val localCurrencyList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United States Dollar"))
                add(Currency("JPY", "Japanese Yen"))
            }
            whenever(localDataSource.getCurrencyList())
                .thenReturn(success(localCurrencyList))

            val remoteExchangeRateList = arrayListOf<ExchangeRate>()
            whenever(remoteDataSource.getExchangeRateList(localCurrencyList))
                .thenReturn(success(remoteExchangeRateList))

            val excludedCurrencyCode = "JPY"
            val localExchangeRateList = arrayListOf<ExchangeRate>()
            whenever(localDataSource.getExchangeRateList(0, 20, excludedCurrencyCode))
                .thenReturn(success(localExchangeRateList))

            val resultList = (repositoryImpl.getExchangeRateList(0, 20, excludedCurrencyCode)
                    as Resource.Success<List<ExchangeRate>>).data

            assertTrue(resultList.isEmpty())

            verify(localDataSource).getCurrencyList()
            verify(remoteDataSource).getExchangeRateList(localCurrencyList)
            verify(localDataSource, never()).storeExchangeRateList(safelyAnyList())
            verify(localDataSource).getExchangeRateList(0, 20, excludedCurrencyCode)
        }
    }

    @Test
    fun `getExchangeRateList, remote exchange rate list not empty`() {
        runBlocking {
            val localCurrencyList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United States Dollar"))
                add(Currency("JPY", "Japanese Yen"))
            }
            whenever(localDataSource.getCurrencyList())
                .thenReturn(success(localCurrencyList))

            val remoteExchangeRateList = arrayListOf<ExchangeRate>().apply {
                add(ExchangeRate("AUD", "USD", 1.3897))
                add(ExchangeRate("CAD", "USD", 1.26352))
            }
            whenever(remoteDataSource.getExchangeRateList(localCurrencyList))
                .thenReturn(success(remoteExchangeRateList))

            val excludedCurrencyCode = "JPY"
            val localExchangeRateList = arrayListOf<ExchangeRate>().apply {
                add(ExchangeRate("AUD", "USD", 1.3897))
                add(ExchangeRate("CAD", "USD", 1.26352))
            }
            whenever(localDataSource.getExchangeRateList(0, 20, excludedCurrencyCode))
                .thenReturn(success(localExchangeRateList))

            val resultList = (repositoryImpl.getExchangeRateList(0, 20, excludedCurrencyCode)
                    as Resource.Success<List<ExchangeRate>>).data

            assertEquals(localExchangeRateList, resultList)

            verify(localDataSource).getCurrencyList()
            verify(remoteDataSource).getExchangeRateList(localCurrencyList)
            verify(localDataSource).storeExchangeRateList(remoteExchangeRateList)
            verify(localDataSource).getExchangeRateList(0, 20, excludedCurrencyCode)
        }
    }

    @Test
    fun `getExchangeRateList, remote data source returns a network error`() {
        runBlocking {
            val localCurrencyList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United States Dollar"))
                add(Currency("JPY", "Japanese Yen"))
            }
            whenever(localDataSource.getCurrencyList())
                .thenReturn(success(localCurrencyList))

            whenever(remoteDataSource.getExchangeRateList(localCurrencyList))
                .thenReturn(error(UnknownHostException()))

            val localExchangeRateList = arrayListOf<ExchangeRate>().apply {
                add(ExchangeRate("AUD", "USD", 1.3897))
                add(ExchangeRate("CAD", "USD", 1.26352))
            }
            val excludedCurrencyCode = "JPY"
            whenever(localDataSource.getExchangeRateList(0, 20, excludedCurrencyCode))
                .thenReturn(success(localExchangeRateList))

            val resultList = (repositoryImpl.getExchangeRateList(0, 20, excludedCurrencyCode)
                    as Resource.Success<List<ExchangeRate>>).data

            assertEquals(localExchangeRateList, resultList)

            verify(localDataSource).getCurrencyList()
            verify(remoteDataSource).getExchangeRateList(localCurrencyList)
            verify(localDataSource, never()).storeExchangeRateList(safelyAnyList())
            verify(localDataSource).getExchangeRateList(0, 20, excludedCurrencyCode)
        }
    }

    @Test
    fun `getExchangeRateList, remote data source returns a timeout error`() {
        runBlocking {
            val localCurrencyList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United States Dollar"))
                add(Currency("JPY", "Japanese Yen"))
            }
            whenever(localDataSource.getCurrencyList())
                .thenReturn(success(localCurrencyList))

            whenever(remoteDataSource.getExchangeRateList(localCurrencyList))
                .thenReturn(error(SocketTimeoutException()))

            val localExchangeRateList = arrayListOf<ExchangeRate>().apply {
                add(ExchangeRate("AUD", "USD", 1.3897))
                add(ExchangeRate("CAD", "USD", 1.26352))
            }
            val excludedCurrencyCode = "JPY"
            whenever(localDataSource.getExchangeRateList(0, 20, excludedCurrencyCode))
                .thenReturn(success(localExchangeRateList))

            val resultList = (repositoryImpl.getExchangeRateList(0, 20, excludedCurrencyCode)
                    as Resource.Success<List<ExchangeRate>>).data

            assertEquals(localExchangeRateList, resultList)

            verify(localDataSource).getCurrencyList()
            verify(remoteDataSource).getExchangeRateList(localCurrencyList)
            verify(localDataSource, never()).storeExchangeRateList(safelyAnyList())
            verify(localDataSource).getExchangeRateList(0, 20, excludedCurrencyCode)
        }
    }

    @Test
    fun `getExchangeRateList, remote data source returns a data parsing error`() {
        runBlocking {
            val localCurrencyList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United States Dollar"))
                add(Currency("JPY", "Japanese Yen"))
            }
            whenever(localDataSource.getCurrencyList())
                .thenReturn(success(localCurrencyList))

            val jsonParsingError = JsonParseException("")
            whenever(remoteDataSource.getExchangeRateList(localCurrencyList))
                .thenReturn(error(jsonParsingError))

            val localExchangeRateList = arrayListOf<ExchangeRate>().apply {
                add(ExchangeRate("AUD", "USD", 1.3897))
                add(ExchangeRate("CAD", "USD", 1.26352))
            }
            val excludedCurrencyCode = "JPY"
            whenever(localDataSource.getExchangeRateList(0, 20, excludedCurrencyCode))
                .thenReturn(success(localExchangeRateList))

            val result = (repositoryImpl.getExchangeRateList(0, 20, excludedCurrencyCode)
                    as Resource.Error<List<ExchangeRate>>).error

            assertEquals(jsonParsingError, result)

            verify(localDataSource).getCurrencyList()
            verify(remoteDataSource).getExchangeRateList(localCurrencyList)
            verify(localDataSource, never()).storeExchangeRateList(safelyAnyList())
            verify(localDataSource, never()).getExchangeRateList(anyInt(), anyInt(), anyString())
        }
    }

    @Test
    fun `getExchangeRate, local data source returns data`() {
        runBlocking {
            val usdToAudExchangeRate = ExchangeRate("AUD", "USD", 1.3897)
            whenever(localDataSource.getExchangeRate("AUD"))
                .thenReturn(success(usdToAudExchangeRate))

            val result = (repositoryImpl.getExchangeRate("AUD")
                    as Resource.Success<ExchangeRate>).data

            assertEquals(usdToAudExchangeRate, result)

            verify(localDataSource).getExchangeRate("AUD")
        }
    }
}