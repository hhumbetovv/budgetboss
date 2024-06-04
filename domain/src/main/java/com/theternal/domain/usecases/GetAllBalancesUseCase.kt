package com.theternal.domain.usecases

import com.theternal.domain.repositories.AccountRepository
import com.theternal.domain.repositories.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import javax.inject.Inject

class GetAllBalancesUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val currencyRepository: CurrencyRepository
) {
    operator fun invoke(): Flow<BigDecimal> {
        return combine(
            accountRepository.getAllBalances(),
            currencyRepository.getCurrencyList()
        ) { entries, currencyList ->

            entries.fold(BigDecimal.ZERO) { acc, entry ->
                val currencyValue = currencyList.find {
                    it.label == entry.currency
                }?.value ?: BigDecimal.ONE

                acc + currencyValue * entry.balance
            }
        }
    }
}