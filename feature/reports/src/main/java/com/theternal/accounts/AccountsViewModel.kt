package com.theternal.accounts

import androidx.lifecycle.viewModelScope
import com.theternal.core.base.BaseViewModel
import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.domain.usecases.GetAllAccountsUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.theternal.accounts.AccountsContract.*
import com.theternal.core.base.interfaces.ViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class AccountsViewModel @Inject constructor(
    getAllAccountsUseCase: GetAllAccountsUseCase,
) : BaseViewModel<ViewEvent.Empty, State, ViewEffect.Empty>() {

    override fun createState(): State {
        return State()
    }

    init {
        getAllAccountsUseCase().onEach { accounts ->
            setState { State(accounts) }
        }.launchIn(viewModelScope)
    }
}