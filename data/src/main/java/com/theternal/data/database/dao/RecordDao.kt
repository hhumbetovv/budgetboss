package com.theternal.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.entities.local.TransferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    //! Financial Records
    @Query("SELECT * FROM financialRecords WHERE id=:id")
    fun getFinancialRecord(id: Long): Flow<FinancialRecordEntity>

    @Query("SELECT * FROM financialRecords")
    fun getAllFinancialRecords(): Flow<List<FinancialRecordEntity>>

    @Query("SELECT * FROM financialRecords WHERE isExpense=:isExpense")
    fun getAllFinancialRecords(isExpense: Boolean): Flow<List<FinancialRecordEntity>>

    @Query("SELECT * FROM financialRecords WHERE isExpense=:isExpense AND title=:title")
    fun getAllFinancialRecords(title: String, isExpense: Boolean): Flow<List<FinancialRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addFinancialRecord(record: FinancialRecordEntity): Long

    @Query("DELETE FROM financialRecords WHERE id=:id")
    fun deleteFinancialRecord(id: Long)

    @Update
    fun updateFinancialRecord(record: FinancialRecordEntity)

    //! Transfer Records
    @Query("SELECT * FROM transferRecords WHERE id=:id")
    fun getTransferRecord(id: Long): Flow<TransferEntity>

    @Query("SELECT * FROM transferRecords")
    fun getAllTransferRecords(): Flow<List<TransferEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addTransferRecord(record: TransferEntity): Long

    @Query("DELETE FROM transferRecords WHERE id=:id")
    fun deleteTransferRecord(id: Long)

    @Update
    fun updateTransferRecord(record: TransferEntity)
}