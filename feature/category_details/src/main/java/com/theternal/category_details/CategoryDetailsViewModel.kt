package com.theternal.category_details

import androidx.lifecycle.viewModelScope
import com.theternal.core.base.BaseViewModel
import com.theternal.category_details.CategoryDetailsContract.*
import com.theternal.domain.usecases.GetCategoryRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategoryDetailsViewModel @Inject constructor(
    private val getCategoryRecordsUseCase: GetCategoryRecordsUseCase
) : BaseViewModel<Event, State, Effect>() {

    private var getRecordsJob: Job? = null

    override fun createState(): State {
        return State()
    }

    override fun onEventUpdate(event: Event) {
        when(event){
            is Event.GetRecords -> getRecords(event.title, event.isExpense)
        }
    }

    private fun getRecords(title: String?, isExpense: Boolean) {
        if(title == null) return
        getRecordsJob?.cancel()
        getRecordsJob = getCategoryRecordsUseCase(title, isExpense).onEach { records ->
            setState { state ->
                state.copy(
                    records = records.sortedByDescending { it.date },
                    totalAmount = records.fold(BigDecimal.ZERO) { acc, record ->
                        acc + record.amount
                    }
                )
            }
            if(records.isEmpty()) postEffect(Effect.NavigateBack)
        }.launchIn(viewModelScope)
    }
}