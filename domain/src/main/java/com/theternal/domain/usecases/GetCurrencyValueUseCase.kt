package com.theternal.domain.usecases

import com.theternal.domain.repositories.CurrencyRepository
import java.math.BigDecimal
import javax.inject.Inject

class GetCurrencyValueUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {

    operator fun invoke(label: String): BigDecimal {
        return currencyRepository.getCurrencyValueAsync(label)
    }
}