package com.theternal.data.repositories

import com.theternal.data.database.dao.AccountDao
import com.theternal.domain.entities.local.AccountBalance
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.repositories.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
): AccountRepository {

    /**
     * Adds a transfer record by updating the sender's and receiver's accounts accordingly.
     *
     * This method retrieves the sender and receiver accounts synchronously from the database,
     * updates their balances based on the transfer amount and exchange value, and appends the transfer ID
     * to their respective transfer lists.
     *
     * @param record The TransferEntity representing the transfer details, including sender ID, receiver ID,
     * amount, and exchange value.
     */
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

    /**
     * Removes a transfer record by reverting the changes made to the sender's and receiver's accounts.
     *
     * This method retrieves the sender and receiver accounts synchronously from the database,
     * updates their balances to revert the transfer amount and exchange value, and removes the transfer ID
     * from their respective transfer lists.
     *
     * @param record The TransferEntity representing the transfer details, including sender ID, receiver ID,
     * amount, and exchange value.
     */
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

    override fun deleteAccount(id: Long) {
        accountDao.deleteAccount(id)
    }

    override fun updateAccount(account: AccountEntity) {
        accountDao.updateAccount(account)
    }
}