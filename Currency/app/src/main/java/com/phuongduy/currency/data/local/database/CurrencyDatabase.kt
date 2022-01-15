package com.phuongduy.currency.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phuongduy.currency.data.local.database.dao.ExchangeRateDao
import com.phuongduy.currency.data.local.database.dao.CurrencyDao
import com.phuongduy.currency.data.local.database.model.ExchangeRateDbModel
import com.phuongduy.currency.data.local.database.model.CurrencyDbModel

@Database(
    entities = [CurrencyDbModel::class, ExchangeRateDbModel::class],
    version = 1
)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun getCurrencyDao(): CurrencyDao
    abstract fun getExchangeRateDao(): ExchangeRateDao
}