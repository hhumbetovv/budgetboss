package com.theternal.domain.repositories

import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.RecordEntity
import kotlinx.coroutines.flow.Flow

interface RecordRepository {
    fun createRecord(record: RecordEntity): Long
    fun getAllRecords(): Flow<List<RecordEntity>>
    fun getAllFinancialRecords(isExpense: Boolean?) : Flow<List<FinancialRecordEntity>>
    fun getAllFinancialRecords(title: String, isExpense: Boolean) : Flow<List<FinancialRecordEntity>>
    fun getAllTransfers(): Flow<List<TransferEntity>>
    fun getRecord(id: Long, isTransfer: Boolean): Flow<RecordEntity>
    fun deleteRecord(id: Long, isTransfer: Boolean)
    fun updateRecord(record: RecordEntity)
}