package com.phuongduy.currency.domain.usecase

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.domain.CurrencyRepository
import com.phuongduy.currency.domain.entity.ExchangeRate
import javax.inject.Inject

class GetExchangeRateListUseCaseImpl @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : GetExchangeRateListUseCase {
    override suspend fun execute(
        skip: Int,
        take: Int,
        excludedCurrencyCode: String
    ): Resource<List<ExchangeRate>> {
        return currencyRepository.getExchangeRateList(skip, take, excludedCurrencyCode)
    }
}