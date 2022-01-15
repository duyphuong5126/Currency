package com.phuongduy.currency.domain.usecase

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.domain.CurrencyRepository
import com.phuongduy.currency.domain.entity.ExchangeRate
import javax.inject.Inject

class GetExchangeRateUseCaseImpl @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : GetExchangeRateUseCase {
    override suspend fun execute(currencyCode: String): Resource<ExchangeRate> {
        return currencyRepository.getExchangeRate(currencyCode)
    }
}