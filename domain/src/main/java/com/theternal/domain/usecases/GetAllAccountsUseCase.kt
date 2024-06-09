package com.theternal.domain.usecases

import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.repositories.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<List<AccountEntity>> {
        return accountRepository.getAllAccounts()
    }
}