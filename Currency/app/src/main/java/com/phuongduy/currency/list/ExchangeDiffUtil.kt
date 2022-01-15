package com.phuongduy.currency.list

import androidx.recyclerview.widget.DiffUtil
import com.phuongduy.currency.presentation.uimodel.ExchangeUiModel

class ExchangeDiffUtil : DiffUtil.ItemCallback<ExchangeUiModel>() {
    override fun areItemsTheSame(oldItem: ExchangeUiModel, newItem: ExchangeUiModel): Boolean {
        return oldItem.currencyName == newItem.currencyName
    }

    override fun areContentsTheSame(oldItem: ExchangeUiModel, newItem: ExchangeUiModel): Boolean {
        return oldItem.currencyName == newItem.currencyName &&
                return oldItem.exchangeAmount == newItem.exchangeAmount
    }
}