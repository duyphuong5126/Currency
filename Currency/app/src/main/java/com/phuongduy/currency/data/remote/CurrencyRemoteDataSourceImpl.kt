package com.phuongduy.currency.data.remote

import com.phuongduy.currency.BuildConfig
import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.coroutine.executeForResult
import com.phuongduy.currency.data.remote.apiservice.CurrencyApiService
import com.phuongduy.currency.domain.entity.Currency
import com.phuongduy.currency.domain.entity.ExchangeRate
import org.json.JSONObject
import javax.inject.Inject

class CurrencyRemoteDataSourceImpl @Inject constructor(
    private val currencyApiService: CurrencyApiService
) : CurrencyRemoteDataSource {
    private val accessKey = BuildConfig.CURRENCY_LAYER_ACCESS_KEY

    override suspend fun getSupportedCurrencies(): Resource<List<Currency>> =
        executeForResult {
            currencyApiService.getSupportedCurrencyList(accessKey)
        }.map { responseBody ->
            val jsonObject = JSONObject(responseBody.string())
            val currenciesObject = jsonObject.getJSONObject("currencies")
            currenciesObject.keys().asSequence().toList().map { key ->
                Currency(key, currenciesObject.getString(key))
            }
        }

    override suspend fun getExchangeRateList(
        supportedCurrencyList: List<Currency>
    ): Resource<List<ExchangeRate>> = executeForResult {
        val currenciesString =
            supportedCurrencyList.map(Currency::currencyCode).joinToString(separator = ",")
        currencyApiService.getExchangeRateList(
            accessKey = accessKey,
            currencies = currenciesString,
            format = 1
        )
    }.map { responseBody ->
        val jsonObject = JSONObject(responseBody.string())
        val exchangeRatesObject = jsonObject.getJSONObject("quotes")
        val source = jsonObject.getString("source")
        val resultList = arrayListOf<ExchangeRate>()
        exchangeRatesObject.keys().asSequence().toList().forEach { key ->
            val exchangeRate = exchangeRatesObject.getDouble(key)
            val targetCurrencyCode = key.replace(source, "")
            if (targetCurrencyCode.isNotBlank()) {
                resultList.add(
                    ExchangeRate(
                        currencyCode = targetCurrencyCode,
                        sourceCurrencyCode = source,
                        fromSourceExchangeRate = exchangeRate
                    )
                )
            } else {
                resultList.add(ExchangeRate(source, source, 1.0))
            }
        }
        resultList
    }
}