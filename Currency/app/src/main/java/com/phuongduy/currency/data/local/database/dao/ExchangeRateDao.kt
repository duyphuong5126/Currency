package com.phuongduy.currency.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phuongduy.currency.data.local.database.model.ExchangeRateDbModel
import com.phuongduy.currency.data.local.database.model.ExchangeRateDbModel.Companion.CURRENCY_CODE
import com.phuongduy.currency.data.local.database.model.ExchangeRateDbModel.Companion.TABLE_NAME

@Dao
interface ExchangeRateDao {
    @Query("select * from $TABLE_NAME where $CURRENCY_CODE != :excludedCurrencyCode limit :take offset :skip")
    suspend fun getExchangeRateList(
        skip: Int,
        take: Int,
        excludedCurrencyCode: String
    ): List<ExchangeRateDbModel>

    @Query("select * from $TABLE_NAME where $CURRENCY_CODE = :currencyCode")
    suspend fun getExchangeRate(currencyCode: String): ExchangeRateDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveExchangeRateList(modelListRate: List<ExchangeRateDbModel>): List<Long>
}