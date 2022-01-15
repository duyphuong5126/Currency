package com.phuongduy.currency.domain

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.domain.entity.Currency
import com.phuongduy.currency.domain.entity.ExchangeRate

interface CurrencyRepository {
    suspend fun getSupportedCurrencies(): Resource<List<Currency>>
    suspend fun getExchangeRateList(
        skip: Int,
        take: Int,
        excludedCurrencyCode: String
    ): Resource<List<ExchangeRate>>

    suspend fun getExchangeRate(currencyCode: String): Resource<ExchangeRate>
}