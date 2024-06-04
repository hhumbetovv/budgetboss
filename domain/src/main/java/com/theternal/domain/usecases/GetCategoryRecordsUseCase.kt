package com.theternal.domain.usecases

import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.repositories.RecordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryRecordsUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(title: String, isExpense: Boolean): Flow<List<FinancialRecordEntity>> {
        return recordRepository.getAllFinancialRecords(
            title,
            isExpense
        )
    }
}