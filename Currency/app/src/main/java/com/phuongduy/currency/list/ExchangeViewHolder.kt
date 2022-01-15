package com.phuongduy.currency.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phuongduy.currency.databinding.ItemExchangeBinding
import com.phuongduy.currency.presentation.uimodel.ExchangeUiModel

class ExchangeViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    ItemExchangeBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
) {
    private val viewBinding = ItemExchangeBinding.bind(itemView)

    fun bindTo(exchangeModel: ExchangeUiModel) {
        viewBinding.currencyName.text = exchangeModel.currencyName
        viewBinding.exchange.text = exchangeModel.exchangeAmount
    }
}