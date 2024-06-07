package com.theternal.record_details

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.theternal.core.base.BaseViewModel
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.domain.usecases.CreateRecordUseCase
import com.theternal.domain.usecases.DeleteRecordUseCase
import com.theternal.domain.usecases.GetRecordUseCase
import com.theternal.domain.usecases.UpdateRecordUseCase
import com.theternal.record_details.RecordDetailsContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordDetailsViewModel @Inject constructor(
    private val getRecordUseCase: GetRecordUseCase,
    private val deleteRecordUseCase: DeleteRecordUseCase,
    private val updateRecordUseCase: UpdateRecordUseCase
) : BaseViewModel<Event, State, Effect>() {

    private var getRecordJob: Job? = null

    override fun createState(): State {
        return State()
    }

    override fun onEventUpdate(event: Event) {
        when(event) {
            Event.DeleteRecord -> deleteRecord()
            is Event.GetRecord -> getRecord(event.id, event.isTransfer)
            is Event.SelectDate -> setState { it.copy(date = event.date) }
            is Event.SetNote -> setState { it.copy(note = event.note) }
            Event.UpdateRecord -> updateRecord()
        }
    }

    private fun getRecord(id: Long, isTransfer: Boolean) {
        getRecordJob?.cancel()
        getRecordJob = getRecordUseCase(id, isTransfer).onEach { record ->
            setState { it.copy(record = record) }
        }.launchIn(viewModelScope)
    }

    private fun deleteRecord() {
        if(currentState.record == null) return
        viewModelScope.launch(Dispatchers.IO) {
            deleteRecordUseCase(currentState.record!!)
            postEffect(Effect.NavigateBack)
        }
    }

    private fun updateRecord() {
        if(currentState.record == null) return
        if(currentState.record is FinancialRecordEntity || currentState.record is TransferEntity) {
            viewModelScope.launch(Dispatchers.IO) {
                updateRecordUseCase(currentState.record!!.copyWith(
                    note = currentState.note,
                    date = currentState.date ?: currentState.record!!.date
                ))
            }
        }
    }

    private fun RecordEntity.copyWith(note: String?, date: Long?): RecordEntity {
        return when (this) {
            is FinancialRecordEntity -> this.copy(note = note, date = date ?: this.date)
            is TransferEntity -> this.copy(note = note, date = date ?: this.date)
            else -> throw IllegalArgumentException("Unknown RecordEntity type")
        }
    }

    override fun onCleared() {
        super.onCleared()
        getRecordJob?.cancel()
    }
}

