package com.phuongduy.currency.domain.usecase

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.domain.entity.Currency

interface GetSupportedCurrenciesUseCase {
    suspend fun execute(): Resource<List<Currency>>
}