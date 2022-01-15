package com.phuongduy.currency.data.local

import com.phuongduy.currency.coroutine.resource.CompletableResource
import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.coroutine.execute
import com.phuongduy.currency.coroutine.executeForResult
import com.phuongduy.currency.data.local.database.dao.ExchangeRateDao
import com.phuongduy.currency.data.local.database.dao.CurrencyDao
import com.phuongduy.currency.data.local.database.model.ExchangeRateDbModel
import com.phuongduy.currency.data.local.database.model.CurrencyDbModel
import com.phuongduy.currency.domain.entity.Currency
import com.phuongduy.currency.domain.entity.ExchangeRate
import javax.inject.Inject

class CurrencyLocalDataSourceImpl @Inject constructor(
    private val currencyDao: CurrencyDao,
    private val exchangeRateDao: ExchangeRateDao
) : CurrencyLocalDataSource {
    override suspend fun getCurrencyList(): Resource<List<Currency>> = executeForResult {
        currencyDao.getCurrencyList()
    }.map { localList ->
        localList.map {
            Currency(it.currencyCode, it.currencyName)
        }
    }

    override suspend fun storeSupportedCurrencyList(currencyList: List<Currency>): CompletableResource =
        execute {
            val modelList = currencyList.map {
                CurrencyDbModel(it.currencyCode, it.currencyName)
            }
            currencyDao.saveCurrencyList(modelList)
        }

    override suspend fun getExchangeRateList(
        skip: Int,
        take: Int,
        excludedCurrencyCode: String
    ): Resource<List<ExchangeRate>> = executeForResult {
        exchangeRateDao.getExchangeRateList(skip, take, excludedCurrencyCode)
    }.map { modelList ->
        modelList.map {
            ExchangeRate(it.currencyCode, it.sourceCurrencyCode, it.fromSourceExchangeRate)
        }
    }

    override suspend fun storeExchangeRateList(
        exchangeRateList: List<ExchangeRate>
    ): CompletableResource = execute {
        exchangeRateDao.saveExchangeRateList(exchangeRateList.map {
            ExchangeRateDbModel(
                it.currencyCode,
                it.sourceCurrencyCode,
                it.fromSourceExchangeRate
            )
        })
    }

    override suspend fun getExchangeRate(currencyCode: String): Resource<ExchangeRate> =
        executeForResult {
            exchangeRateDao.getExchangeRate(currencyCode)
        }.map {
            ExchangeRate(it.currencyCode, it.sourceCurrencyCode, it.fromSourceExchangeRate)
        }
}