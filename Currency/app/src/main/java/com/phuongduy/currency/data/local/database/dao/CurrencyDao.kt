package com.phuongduy.currency.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phuongduy.currency.data.local.database.model.CurrencyDbModel
import com.phuongduy.currency.data.local.database.model.CurrencyDbModel.Companion.TABLE_NAME

@Dao
interface CurrencyDao {
    @Query("select * from $TABLE_NAME")
    suspend fun getCurrencyList(): List<CurrencyDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCurrencyList(modelList: List<CurrencyDbModel>): List<Long>
}