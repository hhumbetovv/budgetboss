package com.theternal.domain.usecases

import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.repositories.AccountRepository
import com.theternal.domain.repositories.RecordRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val recordRepository: RecordRepository,
    private val accountRepository: AccountRepository
) {
    operator fun invoke(account: AccountEntity, records: List<TransferEntity>) {
        records.forEach { record ->
            accountRepository.removeTransfer(record)
            recordRepository.deleteRecord(record.id, true)
        }
        accountRepository.deleteAccount(account.id)
    }
}