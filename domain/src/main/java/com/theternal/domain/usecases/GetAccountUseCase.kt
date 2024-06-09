package com.theternal.domain.usecases

import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.repositories.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(id: Long): Flow<AccountEntity> {
        return accountRepository.getAccount(id)
    }
}