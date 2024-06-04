package com.theternal.domain.usecases

import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.domain.repositories.AccountRepository
import com.theternal.domain.repositories.RecordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateRecordUseCase @Inject constructor(
    private val recordRepository: RecordRepository,
    private val accountRepository: AccountRepository
) {
    operator fun invoke(record: RecordEntity) {
        val id = recordRepository.createRecord(record)

        if(record is TransferEntity) {
            accountRepository.addTransfer(record.copy(id = id))
        }
    }
}