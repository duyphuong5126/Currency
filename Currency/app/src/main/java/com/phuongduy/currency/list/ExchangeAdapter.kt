package com.phuongduy.currency.list

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.phuongduy.currency.presentation.uimodel.ExchangeUiModel

class ExchangeAdapter : PagingDataAdapter<ExchangeUiModel, ExchangeViewHolder>(ExchangeDiffUtil()) {
    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        getItem(position)?.let(holder::bindTo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        return ExchangeViewHolder(parent)
    }
}