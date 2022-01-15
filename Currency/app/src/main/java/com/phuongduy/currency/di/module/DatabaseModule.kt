package com.phuongduy.currency.di.module

import android.app.Application
import androidx.room.Room
import com.phuongduy.currency.data.local.database.DatabaseConstants.DB_NAME
import com.phuongduy.currency.data.local.database.CurrencyDatabase
import com.phuongduy.currency.data.local.database.dao.ExchangeRateDao
import com.phuongduy.currency.data.local.database.dao.CurrencyDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun providesWeatherDB(application: Application): CurrencyDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            CurrencyDatabase::class.java,
            DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesCurrencyDao(currencyDatabase: CurrencyDatabase): CurrencyDao {
        return currencyDatabase.getCurrencyDao()
    }

    @Provides
    fun providesExchangeRateDao(currencyDatabase: CurrencyDatabase): ExchangeRateDao {
        return currencyDatabase.getExchangeRateDao()
    }
}