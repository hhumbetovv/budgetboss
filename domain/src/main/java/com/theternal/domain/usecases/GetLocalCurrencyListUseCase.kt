package com.theternal.domain.usecases

import com.theternal.domain.repositories.CurrencyRepository
import javax.inject.Inject

class GetLocalCurrencyListUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository,
) {
    operator fun invoke(): List<String> {
        val list = currencyRepository.getCurrencyListAsync().map { it.label } as MutableList
        if(!list.contains("USD")) list.add("USD")
        return list
    }
}