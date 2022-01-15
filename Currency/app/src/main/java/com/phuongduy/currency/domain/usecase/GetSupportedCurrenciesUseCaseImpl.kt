package com.phuongduy.currency.domain.usecase

import com.phuongduy.currency.coroutine.resource.Resource
import com.phuongduy.currency.domain.CurrencyRepository
import com.phuongduy.currency.domain.entity.Currency
import javax.inject.Inject

class GetSupportedCurrenciesUseCaseImpl @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : GetSupportedCurrenciesUseCase {
    override suspend fun execute(): Resource<List<Currency>> {
        return currencyRepository.getSupportedCurrencies()
    }
}