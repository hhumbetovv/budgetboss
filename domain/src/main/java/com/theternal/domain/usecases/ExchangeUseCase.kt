package com.theternal.domain.usecases

import com.theternal.domain.repositories.CurrencyRepository
import java.math.BigDecimal
import javax.inject.Inject

class ExchangeUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository,
) {
    suspend operator fun invoke(params: Params): BigDecimal {
        return currencyRepository.exchange(
            params.from,
            params.to
        )
    }

    data class Params(
        val from: String,
        val to: String? = null
    )
}