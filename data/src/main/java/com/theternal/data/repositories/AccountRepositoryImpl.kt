package com.theternal.data.repositories

import android.util.Log
import com.theternal.data.database.dao.AccountDao
import com.theternal.domain.entities.local.AccountBalance
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.repositories.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
): AccountRepository {

    override fun addTransfer(record: TransferEntity) {
        val sender = accountDao.getAccountSync(record.senderId)
        val receiver = accountDao.getAccountSync(record.receiverId)
        accountDao.updateAccount(sender.copy(
            balance = sender.balance - record.amount,
            transfers = sender.transfers + record.id
        ))
        accountDao.updateAccount(receiver.copy(
            balance = receiver.balance + record.amount * record.exchangeValue,
            transfers = receiver.transfers + record.id
        ))
    }

    override fun removeTransfer(record: TransferEntity) {
        val sender = accountDao.getAccountSync(record.senderId)
        val receiver = accountDao.getAccountSync(record.receiverId)
        accountDao.updateAccount(sender.copy(
            balance = sender.balance + record.amount,
            transfers = sender.transfers - record.id
        ))
        accountDao.updateAccount(receiver.copy(
            balance = receiver.balance - record.amount * record.exchangeValue,
            transfers = receiver.transfers - record.id
        ))
    }

    override fun getAllBalances(): Flow<List<AccountBalance>> {
        return accountDao.getAllBalances()
    }

    override fun getAllAccounts(): Flow<List<AccountEntity>> {
        return accountDao.getAllAccounts()
    }

    override fun createAccount(account: AccountEntity) {
        accountDao.addAccount(account)
    }

    override fun getAccount(id: Long): Flow<AccountEntity> {
        return accountDao.getAccount(id)
    }
}