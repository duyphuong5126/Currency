package com.phuongduy.currency.presentation.viewmodel.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.TestObserver
import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.coroutine.resource.Resource.Companion.success
import com.phuongduy.currency.coroutine.resource.Resource.Companion.error
import com.phuongduy.currency.domain.entity.Currency
import com.phuongduy.currency.domain.entity.ExchangeRate
import com.phuongduy.currency.domain.usecase.GetExchangeRateUseCase
import com.phuongduy.currency.domain.usecase.GetExchangeRateListUseCase
import com.phuongduy.currency.domain.usecase.GetSupportedCurrenciesUseCase
import com.phuongduy.currency.presentation.uimodel.CurrencyUiModel
import com.phuongduy.currency.presentation.uimodel.ExchangeUiModel
import com.phuongduy.currency.presentation.utils.MoneyFormatter
import com.phuongduy.currency.presentation.viewmodel.main.MainViewModel.Companion.PAGE_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.anyString
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyDouble
import org.mockito.Mockito.`when` as whenever
import java.net.SocketTimeoutException
import java.sql.SQLException
import kotlin.coroutines.CoroutineContext

class MainViewModelImplTest {
    private val getSupportedCurrenciesUseCase = mock(GetSupportedCurrenciesUseCase::class.java)
    private val getExchangeRateUseCaseListUseCase = mock(GetExchangeRateListUseCase::class.java)
    private val getExchangeRateUseCase = mock(GetExchangeRateUseCase::class.java)
    private val moneyFormatter = mock(MoneyFormatter::class.java)

    private val ioDispatcher = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            block.run()
        }
    }

    private val mainDispatcher = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            block.run()
        }
    }

    private val viewModelImpl = MainViewModelImpl(
        getSupportedCurrenciesUseCase,
        getExchangeRateUseCaseListUseCase,
        getExchangeRateUseCase,
        moneyFormatter,
        ioDispatcher,
        mainDispatcher
    )

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `setUp, no currency found`() {
        runBlocking {
            val currencyList = arrayListOf<Currency>().apply {
                add(Currency("USD", "United States Dollar"))
                add(Currency("JPY", "Japanese Yen"))
            }
            whenever(getSupportedCurrenciesUseCase.execute()).thenReturn(success(currencyList))

            viewModelImpl.setUp()

            TestObserver.test(viewModelImpl.currencyList)
                .assertHasValue()
                .assertValue {
                    assertEquals(2, it.size)
                    assertEquals("USD", it[0].code)
                    assertEquals("United States Dollar", it[0].name)
                    assertEquals("JPY", it[1].code)
                    assertEquals("Japanese Yen", it[1].name)
                    true
                }

            TestObserver.test(viewModelImpl.isLoading)
                .assertHasValue()
                .assertValueHistory(false)
        }
    }

    @Test
    fun `setUp, can not get currency list`() {
        runBlocking {
            whenever(getSupportedCurrenciesUseCase.execute())
                .thenReturn(error(SocketTimeoutException()))

            viewModelImpl.setUp()

            TestObserver.test(viewModelImpl.currencyList)
                .assertNoValue()

            TestObserver.test(viewModelImpl.isLoading)
                .assertHasValue()
                .assertValueHistory(false)
        }
    }

    @Test
    fun `onDataInputted, blank currency code`() {
        viewModelImpl.onDataInputted(CurrencyUiModel("", ""), "1000")

        TestObserver.test(viewModelImpl.isRefreshNeeded)
            .assertHasValue()
            .assertValue(false)
    }

    @Test
    fun `onDataInputted, zero amount`() {
        viewModelImpl.onDataInputted(CurrencyUiModel("USD", "United States Dollar"), "0")

        TestObserver.test(viewModelImpl.isRefreshNeeded)
            .assertHasValue()
            .assertValue(false)
    }

    @Test
    fun `onDataInputted, empty amount text`() {
        viewModelImpl.onDataInputted(CurrencyUiModel("USD", "United States Dollar"), "")

        TestObserver.test(viewModelImpl.isRefreshNeeded)
            .assertHasValue()
            .assertValue(false)
    }

    @Test
    fun `onDataInputted, non blank currency code and non-zero amount`() {
        viewModelImpl.onDataInputted(CurrencyUiModel("USD", "United States Dollar"), "1000")

        TestObserver.test(viewModelImpl.isRefreshNeeded)
            .assertHasValue()
            .assertValue(true)
    }

    @Test
    fun onAmountInputted() {
        viewModelImpl.onAmountInputted("")

        TestObserver.test(viewModelImpl.isRefreshNeeded)
            .assertHasValue()
            .assertValue(false)

        viewModelImpl.onAmountInputted("0")

        TestObserver.test(viewModelImpl.isRefreshNeeded)
            .assertHasValue()
            .assertValue(false)

        viewModelImpl.onAmountInputted("10")

        TestObserver.test(viewModelImpl.isRefreshNeeded)
            .assertHasValue()
            .assertValue(false)
    }

    @Test
    fun `load, blank currency code`() {
        runBlocking {
            viewModelImpl.onDataInputted(CurrencyUiModel("", ""), "1000")

            val resultList = (viewModelImpl.load(0) as Resource.Success<List<ExchangeUiModel>>).data

            assertTrue(resultList.isEmpty())
            verify(getExchangeRateUseCaseListUseCase, never())
                .execute(anyInt(), anyInt(), anyString())
            verify(getExchangeRateUseCase, never()).execute(anyString())
            verify(moneyFormatter, never()).format(anyDouble())
        }
    }

    @Test
    fun `load, zero amount`() {
        runBlocking {
            viewModelImpl.onDataInputted(CurrencyUiModel("JPY", "Japanese Yen"), "0")

            val resultList = (viewModelImpl.load(0) as Resource.Success<List<ExchangeUiModel>>).data

            assertTrue(resultList.isEmpty())

            verify(getExchangeRateUseCaseListUseCase, never())
                .execute(anyInt(), anyInt(), anyString())
            verify(getExchangeRateUseCase, never()).execute(anyString())
            verify(moneyFormatter, never()).format(anyDouble())
        }
    }

    @Test
    fun `load, fail to load exchange rate list`() {
        runBlocking {
            val selectedCurrency = "JPY"
            val error = SocketTimeoutException()
            whenever(getExchangeRateUseCaseListUseCase.execute(0, PAGE_SIZE, selectedCurrency))
                .thenReturn(error(error))
            viewModelImpl.onDataInputted(CurrencyUiModel("JPY", "Japanese Yen"), "1000")

            val errorResult = (viewModelImpl.load(0) as Resource.Error<List<ExchangeUiModel>>).error

            assertEquals(error, errorResult)

            verify(getExchangeRateUseCaseListUseCase).execute(0, PAGE_SIZE, selectedCurrency)
            verify(getExchangeRateUseCase, never()).execute(anyString())
            verify(moneyFormatter, never()).format(anyDouble())
        }
    }

    @Test
    fun `load, fail to load exchange rate of selected currency`() {
        runBlocking {
            val selectedCurrency = "JPY"
            val exchangeList = arrayListOf<ExchangeRate>().apply {
                add(ExchangeRate("AUD", "USD", 1.5))
            }
            whenever(getExchangeRateUseCaseListUseCase.execute(0, PAGE_SIZE, selectedCurrency))
                .thenReturn(success(exchangeList))
            val localError = SQLException()
            whenever(getExchangeRateUseCase.execute(selectedCurrency))
                .thenReturn(error(localError))

            viewModelImpl.onDataInputted(CurrencyUiModel("JPY", "Japanese Yen"), "1000")

            val errorResult = (viewModelImpl.load(0) as Resource.Error<List<ExchangeUiModel>>).error

            assertEquals(localError, errorResult)

            verify(getExchangeRateUseCaseListUseCase).execute(0, PAGE_SIZE, selectedCurrency)
            verify(getExchangeRateUseCase).execute(selectedCurrency)
            verify(moneyFormatter, never()).format(anyDouble())
        }
    }

    @Test
    fun `load, get conversion list successfully`() {
        runBlocking {
            val selectedCurrency = "JPY"
            val exchangeList = arrayListOf<ExchangeRate>().apply {
                add(ExchangeRate("AUD", "USD", 1.3))
            }
            whenever(getExchangeRateUseCaseListUseCase.execute(0, PAGE_SIZE, selectedCurrency))
                .thenReturn(success(exchangeList))

            val selectedExchangeRate = ExchangeRate("JPY", "USD", 114.3)
            whenever(getExchangeRateUseCase.execute(selectedCurrency))
                .thenReturn(success(selectedExchangeRate))

            val jpyToAudExchange = (1.3 / 114.3) * 1000
            whenever(moneyFormatter.format(jpyToAudExchange)).thenReturn("11.374")

            viewModelImpl.onDataInputted(CurrencyUiModel("JPY", "Japanese Yen"), "1000")

            val resultList = (viewModelImpl.load(0) as Resource.Success<List<ExchangeUiModel>>).data

            assertEquals(1, resultList.size)
            assertEquals("AUD", resultList[0].currencyName)
            assertEquals("11.374", resultList[0].exchangeAmount)

            verify(getExchangeRateUseCaseListUseCase).execute(0, PAGE_SIZE, selectedCurrency)
            verify(getExchangeRateUseCase).execute(selectedCurrency)
            verify(moneyFormatter).format(jpyToAudExchange)
        }
    }
}