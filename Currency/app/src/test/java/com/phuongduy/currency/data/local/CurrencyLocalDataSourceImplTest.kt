package com.phuongduy.currency.data.local

import com.phuongduy.currency.coroutine.resource.CompletableResource
import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.data.local.database.dao.ExchangeRateDao
import com.phuongduy.currency.data.local.database.dao.CurrencyDao
import com.phuongduy.currency.data.local.database.model.ExchangeRateDbModel
import com.phuongduy.currency.data.local.database.model.CurrencyDbModel
import com.phuongduy.currency.domain.entity.Currency
import com.phuongduy.currency.domain.entity.ExchangeRate
import com.phuongduy.currency.listThat
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when` as whenever
import java.sql.SQLException

class CurrencyLocalDataSourceImplTest {
    private val currencyDao = mock(CurrencyDao::class.java)
    private val exchangeRateDao = mock(ExchangeRateDao::class.java)

    private val localDataSourceImpl =
        CurrencyLocalDataSourceImpl(currencyDao, exchangeRateDao)

    @Test
    fun `getCurrencyList, DAO returns data`() {
        runBlocking {
            val dbCurrencyList = arrayListOf<CurrencyDbModel>().apply {
                add(CurrencyDbModel("USD", "United State Dollar"))
                add(CurrencyDbModel("JPY", "Japanese Yen"))
                add(CurrencyDbModel("VND", "Vietnamese Dong"))
            }

            whenever(currencyDao.getCurrencyList()).thenReturn(dbCurrencyList)

            val result = localDataSourceImpl.getCurrencyList()

            assertTrue(result is Resource.Success)

            val localCurrencyList = (result as Resource.Success<List<Currency>>).data

            assertEquals("USD", localCurrencyList[0].currencyCode)
            assertEquals("United State Dollar", localCurrencyList[0].currencyName)
            assertEquals("JPY", localCurrencyList[1].currencyCode)
            assertEquals("Japanese Yen", localCurrencyList[1].currencyName)
            assertEquals("VND", localCurrencyList[2].currencyCode)
            assertEquals("Vietnamese Dong", localCurrencyList[2].currencyName)

            verify(currencyDao).getCurrencyList()
        }
    }

    @Test
    fun `getCurrencyList, DAO returns SQL error`() {
        runBlocking {
            val dbError = SQLException("Invalid syntax near where")

            whenever(currencyDao.getCurrencyList()).thenAnswer {
                throw dbError
            }

            val result = localDataSourceImpl.getCurrencyList()

            assertTrue(result is Resource.Error && dbError == result.error)

            verify(currencyDao).getCurrencyList()
        }
    }

    @Test
    fun `storeSupportedCurrencyList, DAO executed insert command successfully`() {
        runBlocking {
            val currencyList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United State Dollar"))
                add(Currency("JPY", "Japanese Yen"))
                add(Currency("VND", "Vietnamese Dong"))
            }

            val result = localDataSourceImpl.storeSupportedCurrencyList(currencyList)

            assertTrue(result is CompletableResource.Completed)

            verify(currencyDao).saveCurrencyList(listThat { currencyDbModelList ->
                assertEquals("USD", currencyDbModelList[0].currencyCode)
                assertEquals("United State Dollar", currencyDbModelList[0].currencyName)
                assertEquals("JPY", currencyDbModelList[1].currencyCode)
                assertEquals("Japanese Yen", currencyDbModelList[1].currencyName)
                assertEquals("VND", currencyDbModelList[2].currencyCode)
                assertEquals("Vietnamese Dong", currencyDbModelList[2].currencyName)
                true
            })
        }
    }

    @Test
    fun `storeSupportedCurrencyList, insert command returns an error`() {
        runBlocking {
            val currencyList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United State Dollar"))
                add(Currency("JPY", "Japanese Yen"))
                add(Currency("VND", "Vietnamese Dong"))
            }
            val dbError = SQLException("DB connection interrupted")
            whenever(currencyDao.saveCurrencyList(listThat { currencyDbModelList ->
                assertEquals("USD", currencyDbModelList[0].currencyCode)
                assertEquals("United State Dollar", currencyDbModelList[0].currencyName)
                assertEquals("JPY", currencyDbModelList[1].currencyCode)
                assertEquals("Japanese Yen", currencyDbModelList[1].currencyName)
                assertEquals("VND", currencyDbModelList[2].currencyCode)
                assertEquals("Vietnamese Dong", currencyDbModelList[2].currencyName)
                true
            })).thenAnswer {
                throw dbError
            }

            val result = localDataSourceImpl.storeSupportedCurrencyList(currencyList)

            assertTrue(result is CompletableResource.Failed && result.error == dbError)

            verify(currencyDao).saveCurrencyList(listThat { currencyDbModelList ->
                assertEquals("USD", currencyDbModelList[0].currencyCode)
                assertEquals("United State Dollar", currencyDbModelList[0].currencyName)
                assertEquals("JPY", currencyDbModelList[1].currencyCode)
                assertEquals("Japanese Yen", currencyDbModelList[1].currencyName)
                assertEquals("VND", currencyDbModelList[2].currencyCode)
                assertEquals("Vietnamese Dong", currencyDbModelList[2].currencyName)
                true
            })
        }
    }

    @Test
    fun `getExchangeRateList, DAO returns data`() {
        runBlocking {
            val localExchangeRateList = arrayListOf<ExchangeRateDbModel>().apply {
                add(ExchangeRateDbModel("AUD", "USD", 1.3897))
                add(ExchangeRateDbModel("CAD", "USD", 1.26352))
                add(ExchangeRateDbModel("PLN", "USD", 4.01575))
            }

            val excludedCurrencyCode = "USD"
            whenever(exchangeRateDao.getExchangeRateList(0, 20, excludedCurrencyCode))
                .thenReturn(localExchangeRateList)

            val result = localDataSourceImpl.getExchangeRateList(0, 20, excludedCurrencyCode)

            val resultList = (result as Resource.Success<List<ExchangeRate>>).data

            assertEquals("AUD", resultList[0].currencyCode)
            assertEquals("USD", resultList[0].sourceCurrencyCode)
            assertEquals(1.3897, resultList[0].fromSourceExchangeRate, 0.0)
            assertEquals("CAD", resultList[1].currencyCode)
            assertEquals("USD", resultList[1].sourceCurrencyCode)
            assertEquals(1.26352, resultList[1].fromSourceExchangeRate, 0.0)
            assertEquals("PLN", resultList[2].currencyCode)
            assertEquals("USD", resultList[2].sourceCurrencyCode)
            assertEquals(4.01575, resultList[2].fromSourceExchangeRate, 0.0)

            verify(exchangeRateDao).getExchangeRateList(0, 20, excludedCurrencyCode)
        }
    }

    @Test
    fun storeExchangeRateList() {
        runBlocking {
            val localExchangeRateList = arrayListOf<ExchangeRate>().apply {
                add(ExchangeRate("AUD", "USD", 1.3897))
                add(ExchangeRate("CAD", "USD", 1.26352))
                add(ExchangeRate("PLN", "USD", 4.01575))
            }

            localDataSourceImpl.storeExchangeRateList(localExchangeRateList)

            verify(exchangeRateDao).saveExchangeRateList(listThat { resultList ->
                assertEquals("AUD", resultList[0].currencyCode)
                assertEquals("USD", resultList[0].sourceCurrencyCode)
                assertEquals(1.3897, resultList[0].fromSourceExchangeRate, 0.0)
                assertEquals("CAD", resultList[1].currencyCode)
                assertEquals("USD", resultList[1].sourceCurrencyCode)
                assertEquals(1.26352, resultList[1].fromSourceExchangeRate, 0.0)
                assertEquals("PLN", resultList[2].currencyCode)
                assertEquals("USD", resultList[2].sourceCurrencyCode)
                assertEquals(4.01575, resultList[2].fromSourceExchangeRate, 0.0)
                true
            })
        }
    }
}