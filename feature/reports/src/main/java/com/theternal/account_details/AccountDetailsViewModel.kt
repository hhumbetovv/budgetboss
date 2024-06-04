package com.theternal.account_details

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.theternal.core.base.BaseViewModel
import com.theternal.account_details.AccountDetailsContract.*
import com.theternal.domain.usecases.GetAccountTransfersUseCase
import com.theternal.domain.usecases.GetAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val getAccountTransfersUseCase: GetAccountTransfersUseCase
) : BaseViewModel<Event, State, Effect>() {

    private var getAccountJob: Job? = null
    private var getRecordsJob: Job? = null

    override fun createState(): State {
        return State()
    }

    override fun onEventUpdate(event: Event) {
        when(event) {
            is Event.GetAccount -> getAccount(event.id)
        }
    }

    private fun getAccount(id: Long?) {
        if(id == null) return
        getAccountJob?.cancel()
        getAccountJob = getAccountUseCase(id).onEach { account ->
            setState(currentState.copy(account = account))
            getRecordsJob?.cancel()
            getRecordsJob = getAccountTransfersUseCase(account.transfers).onEach { transfers ->
                setState(currentState.copy(
                    transfers = transfers.sortedByDescending { it.date }
                ))
            }.launchIn(viewModelScope)
        }.launchIn(viewModelScope)
    }

}