package com.phuongduy.currency.paging

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.presentation.uimodel.ExchangeUiModel

interface ExchangePagingDataSource {
    suspend fun load(pageIndex: Int): Resource<List<ExchangeUiModel>>
}