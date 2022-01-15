package com.phuongduy.currency.di.module

import com.phuongduy.currency.data.CurrencyRepositoryImpl
import com.phuongduy.currency.data.local.CurrencyLocalDataSource
import com.phuongduy.currency.data.local.CurrencyLocalDataSourceImpl
import com.phuongduy.currency.data.remote.CurrencyRemoteDataSource
import com.phuongduy.currency.data.remote.CurrencyRemoteDataSourceImpl
import com.phuongduy.currency.domain.CurrencyRepository
import com.phuongduy.currency.domain.usecase.*
import com.phuongduy.currency.presentation.utils.MoneyFormatter
import com.phuongduy.currency.presentation.utils.MoneyFormatterImpl
import dagger.Binds
import dagger.Module

@Module
interface AppModule {
    @Binds
    fun bindsCurrencyRemoteDataSource(impl: CurrencyRemoteDataSourceImpl): CurrencyRemoteDataSource

    @Binds
    fun bindsCurrencyLocalDataSource(impl: CurrencyLocalDataSourceImpl): CurrencyLocalDataSource

    @Binds
    fun bindsCurrencyRepository(impl: CurrencyRepositoryImpl): CurrencyRepository

    @Binds
    fun bindsGetSupportedCurrenciesUseCase(impl: GetSupportedCurrenciesUseCaseImpl): GetSupportedCurrenciesUseCase

    @Binds
    fun bindsGetExchangeRateListUseCase(impl: GetExchangeRateListUseCaseImpl): GetExchangeRateListUseCase

    @Binds
    fun bindsGetExchangeRateUseCase(impl: GetExchangeRateUseCaseImpl): GetExchangeRateUseCase

    @Binds
    fun bindsMoneyFormatter(impl: MoneyFormatterImpl): MoneyFormatter
}