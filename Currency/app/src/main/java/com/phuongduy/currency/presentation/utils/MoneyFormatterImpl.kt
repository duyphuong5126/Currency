package com.phuongduy.currency.presentation.utils

import java.text.DecimalFormat
import javax.inject.Inject

class MoneyFormatterImpl @Inject constructor() : MoneyFormatter {
    private val currencyFormat = DecimalFormat("#,###.###")

    override fun format(double: Double): String {
        return currencyFormat.format(double)
    }
}