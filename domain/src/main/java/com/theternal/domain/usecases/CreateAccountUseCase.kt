package com.theternal.domain.usecases

import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.repositories.AccountRepository
import com.theternal.domain.repositories.CurrencyRepository
import java.math.BigDecimal
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val currencyRepository: CurrencyRepository
) {
    operator fun invoke(account: AccountEntity, currencyValue: BigDecimal? = null) {
        accountRepository.createAccount(account)

        if(currencyValue != null) {
            currencyRepository.saveCurrency(account.currency, currencyValue)
        }
    }
}