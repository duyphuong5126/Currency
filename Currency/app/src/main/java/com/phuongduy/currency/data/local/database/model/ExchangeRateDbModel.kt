package com.phuongduy.currency.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.phuongduy.currency.data.local.database.model.ExchangeRateDbModel.Companion.CURRENCY_CODE
import com.phuongduy.currency.data.local.database.model.ExchangeRateDbModel.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    indices = [Index(value = [CURRENCY_CODE])]
)
data class ExchangeRateDbModel(
    @PrimaryKey @ColumnInfo(name = CURRENCY_CODE) val currencyCode: String,
    @ColumnInfo(name = SOURCE_CURRENCY_CODE) val sourceCurrencyCode: String,
    @ColumnInfo(name = FROM_SOURCE_EXCHANGE_RATE) val fromSourceExchangeRate: Double
) {

    companion object {
        const val TABLE_NAME = "exchange_rate"

        const val CURRENCY_CODE = "currency_code"
        const val SOURCE_CURRENCY_CODE = "source_currency_name"
        const val FROM_SOURCE_EXCHANGE_RATE = "from_source_exchange_rate"
    }
}
