package com.phuongduy.currency.presentation.uimodel

data class CurrencyUiModel(val code: String, val name: String) {
    override fun toString(): String {
        return String.format("%s - %s", code, name)
    }
}
