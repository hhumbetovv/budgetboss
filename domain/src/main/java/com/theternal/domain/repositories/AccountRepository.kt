package com.theternal.domain.repositories

import com.theternal.domain.entities.local.AccountBalance
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.entities.local.TransferEntity
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun addTransfer(record: TransferEntity)
    fun removeTransfer(record: TransferEntity)

    fun getAllBalances(): Flow<List<AccountBalance>>

    fun getAllAccounts(): Flow<List<AccountEntity>>
    fun createAccount(account: AccountEntity)
    fun getAccount(id: Long): Flow<AccountEntity>
    fun deleteAccount(id: Long)
    fun updateAccount(account: AccountEntity)
}