package com.theternal.domain.usecases

import com.theternal.domain.entities.base.RecordType
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.Logger
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.domain.repositories.AccountRepository
import com.theternal.domain.repositories.RecordRepository
import javax.inject.Inject

class CreateRecordUseCase @Inject constructor(
    private val recordRepository: RecordRepository,
    private val accountRepository: AccountRepository,
    private val loggers: Set<@JvmSuppressWildcards Logger>
) {
    operator fun invoke(record: RecordEntity) {
        val id = recordRepository.createRecord(record)

        if(record is TransferEntity) {
            accountRepository.addTransfer(record.copy(id = id))
        }

        createLog(record)
    }

    private fun createLog(record: RecordEntity) {
        if(record is FinancialRecordEntity) {
            val value = if(record.isExpense) RecordType.EXPENSE else RecordType.INCOME
            loggers.forEach { logger ->
                logger.log(
                    "create_record",
                    "category" to record.title,
                    "record_type" to value.name
                )
            }
        } else {
            loggers.forEach { loggers ->
                loggers.log(
                    "create_record",
                    "record_type" to RecordType.TRANSFER.name
                )
            }
        }
    }
}