package com.phuongduy.currency.data.remote

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.domain.entity.Currency
import com.phuongduy.currency.domain.entity.ExchangeRate

interface CurrencyRemoteDataSource {
    suspend fun getSupportedCurrencies(): Resource<List<Currency>>
    suspend fun getExchangeRateList(supportedCurrencyList: List<Currency>): Resource<List<ExchangeRate>>
}