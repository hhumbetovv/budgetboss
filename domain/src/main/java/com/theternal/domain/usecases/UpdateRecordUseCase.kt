package com.theternal.domain.usecases

import com.theternal.domain.interfaces.RecordEntity
import com.theternal.domain.repositories.RecordRepository
import javax.inject.Inject

class UpdateRecordUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {

    operator fun invoke(record: RecordEntity) {
        recordRepository.updateRecord(record)
    }
}