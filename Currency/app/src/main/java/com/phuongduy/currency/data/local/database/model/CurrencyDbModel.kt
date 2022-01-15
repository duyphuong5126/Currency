package com.phuongduy.currency.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.phuongduy.currency.data.local.database.model.CurrencyDbModel.Companion.CURRENCY_CODE
import com.phuongduy.currency.data.local.database.model.CurrencyDbModel.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    indices = [Index(value = [CURRENCY_CODE])]
)
data class CurrencyDbModel(
    @PrimaryKey @ColumnInfo(name = CURRENCY_CODE) val currencyCode: String,
    @ColumnInfo(name = CURRENCY_NAME) val currencyName: String
) {
    companion object {
        const val TABLE_NAME = "currency"

        const val CURRENCY_CODE = "currency_code"
        const val CURRENCY_NAME = "currency_name"
    }
}
