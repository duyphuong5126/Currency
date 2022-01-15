package com.phuongduy.currency.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.presentation.uimodel.ExchangeUiModel

class ExchangePagingSource(
    private val exchangePagingDataSource: ExchangePagingDataSource
) : PagingSource<Int, ExchangeUiModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ExchangeUiModel> {
        val currentPageNumber = params.key ?: 0
        return when (val result = exchangePagingDataSource.load(currentPageNumber)) {
            is Resource.Success<List<ExchangeUiModel>> -> {
                val nextPageNumber = if (result.data.isNotEmpty()) currentPageNumber + 1 else null
                LoadResult.Page(
                    data = result.data,
                    prevKey = null,
                    nextKey = nextPageNumber
                )
            }
            is Resource.Error<List<ExchangeUiModel>> -> LoadResult.Error(result.error)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ExchangeUiModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}