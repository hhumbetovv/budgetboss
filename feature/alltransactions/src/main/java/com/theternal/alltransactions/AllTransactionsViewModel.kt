package com.theternal.alltransactions

import android.app.Application // Added import
import androidx.lifecycle.viewModelScope
import com.theternal.core.base.BaseViewModel
import com.theternal.alltransactions.AllTransactionsContract.*
import com.theternal.domain.usecases.GetFilteredRecordsUseCase
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
    private val application: Application, // Added application
    private val getFilteredRecordsUseCase: GetFilteredRecordsUseCase
) : BaseViewModel<Event, State, Effect>() {

    private var searchJob: Job? = null
    private val debouncePeriod = 500L // 500ms debounce

    override fun createState(): State {
        return State()
    }

    override fun onEventUpdate(event: Event) {
        when (event) {
            is Event.LoadTransactions -> {
                loadTransactions(
                    currentState.searchQuery,
                    currentState.filterMonthYear,
                    currentState.filterPeriod
                )
            }
            is Event.SearchQueryChanged -> {
                searchJob?.cancel()
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
                setState { State() } 
                loadTransactions(null, null, null)
            }
        }
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
                    setState { it.copy(isLoading = false, transactions = records) }
                }
        }
    }

    private fun updateActiveFilterDescription() {
        val descriptions = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

        currentState.searchQuery?.let { query ->
            descriptions.add(application.getString(R.string.active_filters_label_search_term, query))
        }
        currentState.filterMonthYear?.let { (year, month) ->
            val calendar = Calendar.getInstance().apply { set(year, month, 1) }
            descriptions.add(application.getString(R.string.active_filters_label_month, monthYearFormat.format(calendar.time)))
        }
        currentState.filterPeriod?.let { (start, end) ->
            val startDateFormatted = dateFormat.format(start)
            val endDateFormatted = dateFormat.format(end)
            descriptions.add(application.getString(R.string.active_filters_label_period, startDateFormatted, endDateFormatted))
        }

        if (descriptions.isNotEmpty()) {
            val separator = application.getString(R.string.active_filters_separator)
            setState { it.copy(activeFilterDescription = descriptions.joinToString(separator)) }
        } else {
            setState { it.copy(activeFilterDescription = null) }
        }
    }

    init {
        loadTransactions(null, null, null)
        updateActiveFilterDescription()
    }
}
