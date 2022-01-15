package com.phuongduy.currency.presentation.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyFormatterImplTest {
    @Test
    fun format() {
        val moneyFormatterImpl = MoneyFormatterImpl()

        assertEquals("1", moneyFormatterImpl.format(1.0))
        assertEquals("0.012", moneyFormatterImpl.format(0.012345678))
        assertEquals("1,234,567.891", moneyFormatterImpl.format(1234567.89123456))
    }
}