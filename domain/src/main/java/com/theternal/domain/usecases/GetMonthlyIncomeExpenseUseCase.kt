package com.theternal.domain.usecases

import com.theternal.domain.entities.aggregations.MonthlySummary
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.repositories.RecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.util.Calendar
import javax.inject.Inject

class GetMonthlyIncomeExpenseUseCase @Inject constructor(
    private val recordRepository: RecordRepository // Using existing repository
) {
    operator fun invoke(): Flow<MonthlySummary> {
        // Get current month and year
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) // 0-indexed

        // Define start and end timestamps for the current month
        val monthStartCalendar = Calendar.getInstance().apply {
            set(currentYear, currentMonth, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = monthStartCalendar.timeInMillis

        val monthEndCalendar = Calendar.getInstance().apply {
            set(currentYear, currentMonth, 1, 23, 59, 59) // End of day for the first day
            set(Calendar.MILLISECOND, 999)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) // Last day of current month
        }
        val endTime = monthEndCalendar.timeInMillis
        
        return recordRepository.getAllRecords().map { records ->
            var income = BigDecimal.ZERO
            var expense = BigDecimal.ZERO
            records.filterIsInstance<FinancialRecordEntity>()
                .filter { record ->
                    record.date in startTime..endTime // Filter by date range
                }
                .forEach { record ->
                    if (record.isExpense) {
                        expense += record.amount
                    } else {
                        income += record.amount
                    }
                }
            MonthlySummary(totalIncome = income, totalExpense = expense)
        }
    }
}
