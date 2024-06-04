package com.theternal.domain.usecases

import com.theternal.domain.interfaces.RecordEntity
import com.theternal.domain.repositories.RecordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllRecordsUseCase @Inject constructor(
    private val recordRepository: RecordRepository,
) {
    operator fun invoke(): Flow<List<RecordEntity>> {
        return recordRepository.getAllRecords()
    }
}