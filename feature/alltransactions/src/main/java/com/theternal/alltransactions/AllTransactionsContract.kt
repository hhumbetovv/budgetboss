package com.theternal.alltransactions

import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.core.base.interfaces.ViewState
import com.theternal.domain.interfaces.RecordEntity

sealed interface AllTransactionsContract {

    sealed interface Event : ViewEvent {
        data object LoadTransactions : Event // Existing
        data class SearchQueryChanged(val query: String) : Event
        data object FilterByMonthClicked : Event
        data object FilterByPeriodClicked : Event
        data class MonthSelected(val year: Int, val month: Int) : Event // month is 0-indexed
        data class PeriodSelected(val startDate: Long, val endDate: Long) : Event
        data object ClearSearchAndFilters : Event
    }

    data class State(
        val isLoading: Boolean = false,
        val transactions: List<RecordEntity> = emptyList(),
        val error: String? = null,
        val searchQuery: String? = null,
        val filterMonthYear: Pair<Int, Int>? = null, // Year, Month (0-indexed)
        val filterPeriod: Pair<Long, Long>? = null,   // StartDate, EndDate
        val activeFilterDescription: String? = null
    ) : ViewState

    sealed interface Effect : ViewEffect {
        data object ShowMonthPicker : Effect
        data object ShowDateRangePicker : Effect
        // Future: data class ShowTransactionDetails(val transactionId: Long) : Effect
    }
}
