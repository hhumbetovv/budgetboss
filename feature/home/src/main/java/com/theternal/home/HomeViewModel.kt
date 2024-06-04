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
) : BaseViewModel<ViewEvent.Empty, State, Effect>() {

    override fun createState(): State {
        return State()
    }

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            makeRequest(
                NetworkRequest.NoParams(fetchCurrencyValuesUseCase::invoke),
                onError = {
                    postEffect(Effect.FetchFailedNotify)
                }
            )
        }
        combine(
            getAllRecordsUseCase(),
            getAllBalancesUseCase(),
        ) { records, balance ->
            State(
                balance = balance + getAmounts(records),
                records = records.sortedByDescending { it.date }
            )
        }.onEach { state ->
            setState(state)
        }.launchIn(viewModelScope)
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