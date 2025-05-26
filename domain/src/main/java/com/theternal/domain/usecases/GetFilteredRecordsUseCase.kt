package com.theternal.domain.usecases

import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.domain.repositories.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetFilteredRecordsUseCase @Inject constructor(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(
        searchQuery: String?,
        filterYearMonth: Pair<Int, Int>?, // Year, Month (0-indexed)
        filterPeriod: Pair<Long, Long>?   // StartDate, EndDate
    ): Flow<List<RecordEntity>> {
        return recordRepository.getAllRecords().map { records ->
            var filteredRecords = records

            // 1. Filter by Date (Month/Year or Period)
            val (dateFilteredRecords, effectiveStartTime, effectiveEndTime) = when {
                filterYearMonth != null -> {
                    val (year, month) = filterYearMonth
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, 1, 0, 0, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val startTime = calendar.timeInMillis
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    calendar.set(Calendar.MILLISECOND, 999)
                    val endTime = calendar.timeInMillis
                    Triple(records.filter { it.date in startTime..endTime }, startTime, endTime)
                }
                filterPeriod != null -> {
                     val (startTime, endTime) = filterPeriod
                    Triple(records.filter { it.date in startTime..endTime }, startTime, endTime)
                }
                else -> Triple(records, null, null)
            }
            filteredRecords = dateFilteredRecords

            // 2. Filter by Search Query (Text)
            if (!searchQuery.isNullOrBlank()) {
                val lowerCaseQuery = searchQuery.lowercase().trim()
                filteredRecords = filteredRecords.filter { record ->
                    when (record) {
                        is FinancialRecordEntity -> {
                            record.title.lowercase().contains(lowerCaseQuery) ||
                            (record.note?.lowercase()?.contains(lowerCaseQuery) == true)
                            // Potentially add category name if accessible and needed
                        }
                        is TransferEntity -> {
                            record.title?.lowercase()?.contains(lowerCaseQuery) == true ||
                            (record.note?.lowercase()?.contains(lowerCaseQuery) == true)
                        }
                        else -> false
                    }
                }
            }
            
            // Return sorted by date descending
            filteredRecords.sortedByDescending { it.date }
        }
    }
}
