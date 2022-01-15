package com.phuongduy.currency.data

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.coroutine.resource.onErrorResume
import com.phuongduy.currency.data.local.CurrencyLocalDataSource
import com.phuongduy.currency.data.remote.CurrencyRemoteDataSource
import com.phuongduy.currency.domain.CurrencyRepository
import com.phuongduy.currency.domain.entity.Currency
import com.phuongduy.currency.domain.entity.ExchangeRate
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val localDataSource: CurrencyLocalDataSource,
    private val remoteDataSource: CurrencyRemoteDataSource
) : CurrencyRepository {
    override suspend fun getSupportedCurrencies(): Resource<List<Currency>> {
        return remoteDataSource.getSupportedCurrencies()
            .doOnSuccess { remoteSupportedCurrencies ->
                if (remoteSupportedCurrencies.isNotEmpty()) {
                    localDataSource.storeSupportedCurrencyList(remoteSupportedCurrencies)
                }
            }.onErrorResume {
                if (it.isNetworkError() || it.isTimeoutException()) {
                    localDataSource.getCurrencyList()
                } else {
                    Resource.Error(it)
                }
            }
    }

    override suspend fun getExchangeRateList(
        skip: Int,
        take: Int,
        excludedCurrencyCode: String
    ): Resource<List<ExchangeRate>> {
        return localDataSource.getCurrencyList()
            .flatMap { currencyList ->
                if (currencyList.isEmpty()) {
                    Resource.Success(emptyList())
                } else {
                    remoteDataSource.getExchangeRateList(currencyList)
                }
            }
            .doOnSuccess { remoteExchangeRateList ->
                if (remoteExchangeRateList.isNotEmpty()) {
                    localDataSource.storeExchangeRateList(remoteExchangeRateList)
                }
            }.flatMap {
                localDataSource.getExchangeRateList(skip, take, excludedCurrencyCode)
            }
            .onErrorResume {
                if (it.isNetworkError() || it.isTimeoutException()) {
                    localDataSource.getExchangeRateList(skip, take, excludedCurrencyCode)
                } else {
                    Resource.Error(it)
                }
            }
    }

    override suspend fun getExchangeRate(currencyCode: String): Resource<ExchangeRate> {
        return localDataSource.getExchangeRate(currencyCode)
    }
}