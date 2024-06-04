package com.theternal.domain.usecases

import com.theternal.domain.repositories.CurrencyRepository
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class FetchCurrencyValuesUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
){
    suspend operator fun invoke() {
        currencyRepository.getCurrencyListAsync().forEach { currency ->
            val currentValue = currencyRepository.exchange(currency.label, null)
            currencyRepository.saveCurrency(currency.label, currentValue)
        }
    }
}