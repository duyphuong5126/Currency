package com.phuongduy.currency.di.module

import android.app.Application
import com.google.gson.GsonBuilder
import com.phuongduy.currency.data.remote.apiservice.ApiConstants.BASE_URL
import com.phuongduy.currency.data.remote.apiservice.ApiConstants.CONNECT_TIME_OUT
import com.phuongduy.currency.data.remote.apiservice.ApiConstants.NETWORK_CACHE_MAX_AGE
import com.phuongduy.currency.data.remote.apiservice.ApiConstants.NETWORK_CACHE_MAX_STALE
import com.phuongduy.currency.data.remote.apiservice.ApiConstants.NETWORK_CACHE_SIZE
import com.phuongduy.currency.data.remote.apiservice.ApiConstants.READ_TIME_OUT
import com.phuongduy.currency.data.remote.apiservice.CurrencyApiService
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit


@Module
class NetworkModule {
    @Provides
    fun providesOkHttpClient(application: Application): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val cacheControl = CacheControl.Builder()
            .maxAge(NETWORK_CACHE_MAX_AGE, TimeUnit.HOURS)
            .maxStale(NETWORK_CACHE_MAX_STALE, TimeUnit.HOURS)
            .build()

        val cacheInterceptor = Interceptor {
            Timber.d("OkHttp>>> cache interceptor")
            it.request()
                .newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
                .let(it::proceed)
        }
        val networkCache = Cache(application.cacheDir, NETWORK_CACHE_SIZE)
        return OkHttpClient.Builder()
            .cache(networkCache)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(cacheInterceptor)
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder().create()
        val gsonConverterFactory = GsonConverterFactory.create(gson)
        val retrofitBuilder = Retrofit.Builder().addConverterFactory(gsonConverterFactory)

        retrofitBuilder.client(okHttpClient)
        retrofitBuilder.baseUrl(BASE_URL)
        return retrofitBuilder.build()
    }

    @Provides
    fun providesCurrencyApiService(retrofit: Retrofit): CurrencyApiService {
        return retrofit.create(CurrencyApiService::class.java)
    }
}