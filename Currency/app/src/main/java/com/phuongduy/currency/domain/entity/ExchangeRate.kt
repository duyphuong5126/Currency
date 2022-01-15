package com.phuongduy.currency.domain.entity

data class ExchangeRate(
    val currencyCode: String,
    val sourceCurrencyCode: String,
    val fromSourceExchangeRate: Double
)
