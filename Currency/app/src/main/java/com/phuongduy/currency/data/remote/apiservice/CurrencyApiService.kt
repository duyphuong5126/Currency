package com.phuongduy.currency.data.remote.apiservice

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("/list")
    suspend fun getSupportedCurrencyList(@Query("access_key") accessKey: String): ResponseBody

    @GET("/live")
    suspend fun getExchangeRateList(
        @Query("access_key") accessKey: String,
        @Query("currencies") currencies: String,
        @Query("format") format: Int,
    ): ResponseBody
}