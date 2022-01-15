package com.phuongduy.currency.list

import com.phuongduy.currency.presentation.uimodel.ExchangeUiModel
import org.junit.Assert
import org.junit.Test

class ExchangeDiffUtilTest {
    private val diffUtil = ExchangeDiffUtil()

    @Test
    fun areItemsTheSame() {
        diffUtil.areItemsTheSame(
            ExchangeUiModel("AUD", "1000"),
            ExchangeUiModel("JPY", "1000")
        ).let(Assert::assertFalse)

        diffUtil.areItemsTheSame(
            ExchangeUiModel("JPY", "1000"),
            ExchangeUiModel("JPY", "1000")
        ).let(Assert::assertTrue)
    }

    @Test
    fun areContentsTheSame() {
        diffUtil.areContentsTheSame(
            ExchangeUiModel("AUD", "1000"),
            ExchangeUiModel("JPY", "1000")
        ).let(Assert::assertFalse)

        diffUtil.areContentsTheSame(
            ExchangeUiModel("JPY", "1000"),
            ExchangeUiModel("JPY", "2000")
        ).let(Assert::assertFalse)

        diffUtil.areContentsTheSame(
            ExchangeUiModel("AUD", "1000"),
            ExchangeUiModel("JPY", "2000")
        ).let(Assert::assertFalse)

        diffUtil.areContentsTheSame(
            ExchangeUiModel("JPY", "1000"),
            ExchangeUiModel("JPY", "1000")
        ).let(Assert::assertTrue)
    }
}