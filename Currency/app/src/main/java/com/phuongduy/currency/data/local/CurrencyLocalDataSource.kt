package com.phuongduy.currency.data.local

import com.phuongduy.currency.coroutine.resource.CompletableResource
import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.domain.entity.Currency
import com.phuongduy.currency.domain.entity.ExchangeRate

interface CurrencyLocalDataSource {
    suspend fun getCurrencyList(): Resource<List<Currency>>
    suspend fun storeSupportedCurrencyList(currencyList: List<Currency>): CompletableResource
    suspend fun getExchangeRateList(
        skip: Int,
        take: Int,
        excludedCurrencyCode: String
    ): Resource<List<ExchangeRate>>

    suspend fun getExchangeRate(currencyCode: String): Resource<ExchangeRate>

    suspend fun storeExchangeRateList(exchangeRateList: List<ExchangeRate>): CompletableResource
}