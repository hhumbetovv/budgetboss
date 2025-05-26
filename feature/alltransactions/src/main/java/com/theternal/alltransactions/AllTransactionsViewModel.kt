package com.theternal.alltransactions

import androidx.lifecycle.viewModelScope
import com.theternal.core.base.BaseViewModel
import com.theternal.alltransactions.AllTransactionsContract.*
import com.theternal.domain.usecases.GetFilteredRecordsUseCase // Changed from GetAllRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AllTransactionsViewModel @Inject constructor(
    private val getFilteredRecordsUseCase: GetFilteredRecordsUseCase // Changed
) : BaseViewModel<Event, State, Effect>() {

    private var searchJob: Job? = null
    private val debouncePeriod = 500L // 500ms debounce

    override fun createState(): State {
        return State()
    }

    override fun onEventUpdate(event: Event) {
        when (event) {
            is Event.LoadTransactions -> { // Initial load or refresh
                // Ensure current filters are applied even on a manual LoadTransactions event
                loadTransactions(
                    currentState.searchQuery,
                    currentState.filterMonthYear,
                    currentState.filterPeriod
                )
            }
            is Event.SearchQueryChanged -> {
                searchJob?.cancel() // Cancel previous job
                val query = event.query.ifBlank { null }
                setState { it.copy(searchQuery = query) }
                searchJob = viewModelScope.launch {
                    delay(debouncePeriod)
                    loadTransactions(query, currentState.filterMonthYear, currentState.filterPeriod)
                }
            }
            is Event.FilterByMonthClicked -> {
                viewModelScope.launch { postEffect(Effect.ShowMonthPicker) }
            }
            is Event.FilterByPeriodClicked -> {
                viewModelScope.launch { postEffect(Effect.ShowDateRangePicker) }
            }
            is Event.MonthSelected -> {
                setState { it.copy(filterMonthYear = Pair(event.year, event.month), filterPeriod = null) }
                loadTransactions(currentState.searchQuery, currentState.filterMonthYear, null)
            }
            is Event.PeriodSelected -> {
                setState { it.copy(filterPeriod = Pair(event.startDate, event.endDate), filterMonthYear = null) }
                loadTransactions(currentState.searchQuery, null, currentState.filterPeriod)
            }
            is Event.ClearSearchAndFilters -> {
                setState { State() } // Reset to initial state (or keep isLoading if needed for immediate reload)
                loadTransactions(null, null, null) // Load all transactions
            }
        }
        // Update active filter description after any state change that might affect it
        updateActiveFilterDescription()
    }

    private fun loadTransactions(
        query: String?,
        yearMonth: Pair<Int, Int>?,
        period: Pair<Long, Long>?
    ) {
        viewModelScope.launch {
            getFilteredRecordsUseCase(query, yearMonth, period)
                .onStart { setState { it.copy(isLoading = true, error = null) } }
                .catch { throwable ->
                    setState { it.copy(isLoading = false, error = throwable.message ?: "An unknown error occurred") }
                }
                .collect { records ->
                    // ViewModel should not sort, use case already sorts
                    setState { it.copy(isLoading = false, transactions = records) }
                }
        }
    }

    private fun updateActiveFilterDescription() {
        val descriptions = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

        currentState.searchQuery?.let {
            descriptions.add("Search: \"$it\"") // TODO: Use string resource
        }
        currentState.filterMonthYear?.let { (year, month) ->
            val calendar = Calendar.getInstance().apply { set(year, month, 1) }
            descriptions.add("Month: ${monthYearFormat.format(calendar.time)}") // TODO: Use string resource
        }
        currentState.filterPeriod?.let { (start, end) ->
            descriptions.add("Period: ${dateFormat.format(start)} - ${dateFormat.format(end)}") // TODO: Use string resource
        }

        if (descriptions.isNotEmpty()) {
            setState { it.copy(activeFilterDescription = descriptions.joinToString("; ")) }
        } else {
            setState { it.copy(activeFilterDescription = null) }
        }
    }

    // Call this when ViewModel is initialized if you want to load data immediately
    init {
        loadTransactions(null, null, null) // Load all initially
        updateActiveFilterDescription() // Ensure description is set based on initial state
    }
}
