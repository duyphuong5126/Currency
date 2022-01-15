package com.phuongduy.currency.presentation.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.di.qualifier.IODispatcher
import com.phuongduy.currency.di.qualifier.MainDispatcher
import com.phuongduy.currency.domain.usecase.GetExchangeRateUseCase
import com.phuongduy.currency.domain.usecase.GetExchangeRateListUseCase
import com.phuongduy.currency.domain.usecase.GetSupportedCurrenciesUseCase
import com.phuongduy.currency.presentation.uimodel.CurrencyUiModel
import com.phuongduy.currency.presentation.uimodel.ExchangeUiModel
import com.phuongduy.currency.presentation.viewmodel.main.MainViewModel.Companion.PAGE_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat
import javax.inject.Inject

class MainViewModelImpl @Inject constructor(
    private val getSupportedCurrenciesUseCase: GetSupportedCurrenciesUseCase,
    private val getExchangeRateListUseCase: GetExchangeRateListUseCase,
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
    @IODispatcher private val ioDisPatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDisPatcher: CoroutineDispatcher
) : ViewModel(), MainViewModel {

    private val _currencyList = MutableLiveData<List<CurrencyUiModel>>()
    override val currencyList: LiveData<List<CurrencyUiModel>> = _currencyList

    private val _isRefreshNeeded = MutableLiveData<Boolean>()
    override val isRefreshNeeded: LiveData<Boolean> = _isRefreshNeeded

    private var selectedCurrency = ""
    private var selectedAmount = 0

    private val currencyFormat = DecimalFormat("#,###.###")

    private val ioScope = CoroutineScope(ioDisPatcher)
    private val mainScope = CoroutineScope(mainDisPatcher)

    override fun setUp() {
        ioScope.launch {
            getSupportedCurrenciesUseCase.execute()
                .doOnSuccess {
                    val currencyUiModelList = it.map { currency ->
                        CurrencyUiModel(currency.currencyCode, currency.currencyName)
                    }
                    mainScope.launch {
                        _currencyList.value = currencyUiModelList
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            ioScope.cancel()
            mainScope.cancel()
        } catch (error: Throwable) {
            Timber.d("Cannot cancel coroutine scopes with error $error")
        }
    }

    override fun onDataInputted(selectedCurrencyUiModel: CurrencyUiModel, amount: Int) {
        selectedCurrency = selectedCurrencyUiModel.code
        selectedAmount = amount
        checkAndRefreshList()
    }

    override fun onAmountInputted(amount: Int) {
        selectedAmount = amount
        checkAndRefreshList()
    }

    override suspend fun load(pageIndex: Int): Resource<List<ExchangeUiModel>> {
        val skip = pageIndex * PAGE_SIZE
        return if (selectedCurrency.isBlank() || selectedAmount == 0) {
            Resource.Success(emptyList())
        } else {
            getExchangeRateListUseCase.execute(skip, PAGE_SIZE, selectedCurrency)
                .zipWith {
                    getExchangeRateUseCase.execute(selectedCurrency)
                }
                .map {
                    it.first.map { exchangeRate ->
                        val conversionRate =
                            exchangeRate.fromSourceExchangeRate / it.second.fromSourceExchangeRate
                        val conversion = conversionRate * selectedAmount
                        ExchangeUiModel(
                            exchangeRate.currencyCode,
                            currencyFormat.format(conversion)
                        )
                    }
                }
        }
    }

    private fun checkAndRefreshList() {
        _isRefreshNeeded.value = selectedCurrency.isNotBlank() && selectedAmount > 0
    }
}