package com.theternal.data.repositories

import com.theternal.data.database.dao.RecordDao
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.domain.repositories.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao,
) : RecordRepository {

    override fun createRecord(record: RecordEntity): Long {
        return if(record is FinancialRecordEntity) {
            recordDao.addFinancialRecord(record)
        } else {
            recordDao.addTransferRecord(record as TransferEntity)
        }
    }

    override fun getAllRecords(): Flow<List<RecordEntity>> {
        return combine(
            recordDao.getAllTransferRecords(),
            recordDao.getAllFinancialRecords(),
        ) { transfers, financeEntries ->
            (transfers + financeEntries).sortedByDescending { it.date }
        }
    }

    override fun getAllFinancialRecords(
        isExpense: Boolean?
    ): Flow<List<FinancialRecordEntity>> {
        if(isExpense == null) return recordDao.getAllFinancialRecords()
        return recordDao.getAllFinancialRecords(isExpense)
    }

    override fun getAllFinancialRecords(
        title: String,
        isExpense: Boolean
    ): Flow<List<FinancialRecordEntity>> {
        return recordDao.getAllFinancialRecords(title, isExpense)
    }

    override fun getAllTransfers(): Flow<List<TransferEntity>> {
        return recordDao.getAllTransferRecords()
    }

    override fun getRecord(id: Long, isTransfer: Boolean): Flow<RecordEntity> {
        if(isTransfer) return recordDao.getTransferRecord(id)
        return recordDao.getFinancialRecord(id)
    }

    override fun deleteRecord(id: Long, isTransfer: Boolean) {
        if(isTransfer) recordDao.deleteTransferRecord(id)
        else recordDao.deleteFinancialRecord(id)
    }

    override fun updateRecord(record: RecordEntity) {
        if(record is FinancialRecordEntity) {
            recordDao.updateFinancialRecord(record)
        } else {
            recordDao.updateTransferRecord(record as TransferEntity)
        }
    }
}