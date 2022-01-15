package com.phuongduy.currency.domain.usecase

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.domain.entity.ExchangeRate

interface GetExchangeRateListUseCase {
    suspend fun execute(
        skip: Int,
        take: Int,
        excludedCurrencyCode: String
    ): Resource<List<ExchangeRate>>
}