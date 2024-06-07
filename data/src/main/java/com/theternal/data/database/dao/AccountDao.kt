package com.theternal.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.theternal.domain.entities.local.AccountBalance
import com.theternal.domain.entities.local.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    //! Account
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addAccount(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE id=:id")
    fun getAccount(id: Long): Flow<AccountEntity>

    @Query("SELECT * FROM accounts WHERE id=:id")
    fun getAccountSync(id: Long): AccountEntity

    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Update
    fun updateAccount(account: AccountEntity)

    @Query("DELETE FROM accounts WHERE id=:id")
    fun deleteAccount(id: Long)

    //! Amount
    @Query("SELECT currency, balance FROM accounts")
    fun getAllBalances(): Flow<List<AccountBalance>>
}