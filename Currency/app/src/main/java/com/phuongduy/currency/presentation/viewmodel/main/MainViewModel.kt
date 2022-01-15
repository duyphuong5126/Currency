package com.phuongduy.currency.presentation.viewmodel.main

import androidx.lifecycle.LiveData
import com.phuongduy.currency.paging.ExchangePagingDataSource
import com.phuongduy.currency.presentation.uimodel.CurrencyUiModel

interface MainViewModel : ExchangePagingDataSource {
    val currencyList: LiveData<List<CurrencyUiModel>>
    val isRefreshNeeded: LiveData<Boolean>
    val isLoading: LiveData<Boolean>

    fun setUp()

    fun onDataInputted(selectedCurrencyUiModel: CurrencyUiModel, amountText: String)

    fun onAmountInputted(amountText: String)

    companion object {
        const val PAGE_SIZE = 25
    }
}