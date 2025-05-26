package com.theternal.home

import androidx.lifecycle.viewModelScope
import com.theternal.core.base.BaseViewModel
import com.theternal.core.base.interfaces.*
import com.theternal.core.domain.NetworkRequest
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.domain.usecases.FetchCurrencyValuesUseCase
import com.theternal.domain.usecases.GetAllBalancesUseCase
import com.theternal.domain.usecases.GetAllRecordsUseCase
import com.theternal.domain.usecases.GetMonthlyIncomeExpenseUseCase // Add this import
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import com.theternal.home.HomeContract.*

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchCurrencyValuesUseCase: FetchCurrencyValuesUseCase,
    private val getAllRecordsUseCase: GetAllRecordsUseCase,
    private val getAllBalancesUseCase: GetAllBalancesUseCase,
    private val getMonthlyIncomeExpenseUseCase: GetMonthlyIncomeExpenseUseCase // Add this
) : BaseViewModel<ViewEvent.Empty, State, Effect>() {

    private const val LATEST_TRANSACTIONS_LIMIT = 10

    override fun createState(): State {
        return State()
    }

    init {
        updateCurrencyList()
        fetchData() // Changed from getRecordsAndBalances()
    }

    private fun fetchData() {
        combine(
            getAllRecordsUseCase(),
            getAllBalancesUseCase(),
            getMonthlyIncomeExpenseUseCase() // Add the new use case here
        ) { allRecords, balance, monthlySummary -> // Add monthlySummary to lambda params
            val sortedRecords = allRecords.sortedByDescending { it.date }
            State(
                balance = balance + getAmounts(allRecords),
                records = sortedRecords.take(LATEST_TRANSACTIONS_LIMIT),
                monthlySummary = monthlySummary // Populate the new state field
            )
        }.onEach { state ->
            setState { state }
        }.launchIn(viewModelScope)
    }

    private fun updateCurrencyList() {
        viewModelScope.launch(Dispatchers.IO) {
            makeRequest(
                NetworkRequest.NoParams(fetchCurrencyValuesUseCase::invoke),
                onError = {
                    postEffect(Effect.FetchFailedNotify)
                }
            )
        }
    }

    private fun getAmounts(records: List<RecordEntity>): BigDecimal {
        return records.filterIsInstance<FinancialRecordEntity>()
            .fold(BigDecimal.ZERO) { acc, record ->
            if(!record.isExpense) {
                acc + record.amount
            } else {
                acc - record.amount
            }
        }
    }
}