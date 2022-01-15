package com.phuongduy.currency.domain.usecase

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.domain.entity.ExchangeRate

interface GetExchangeRateUseCase {
    suspend fun execute(currencyCode: String): Resource<ExchangeRate>
}