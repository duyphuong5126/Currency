package com.phuongduy.currency.presentation.uimodel

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyUiModelTest {
    @Test
    fun `toString test`() {
        CurrencyUiModel("USD", "United States Dollar")
            .toString()
            .let {
                assertEquals("USD - United States Dollar", it)
            }
    }
}