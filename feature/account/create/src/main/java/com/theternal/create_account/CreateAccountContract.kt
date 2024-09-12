package com.theternal.create_account

import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.core.base.interfaces.ViewState
import java.math.BigDecimal

sealed interface CreateAccountContract {
    sealed interface Event : ViewEvent {

        data class SetAccountName(val accountName: String) : Event

        data class SetBalance(val balance: BigDecimal?) : Event

        data class SetCurrency(val currency: String) : Event

        data class CreateAccount(val note: String? = null) : Event
    }

    data class State(
        val accountName: String = "",
        val balance: BigDecimal? = null,
        val currency: String? = null,
        val currencyList: List<String> = listOf(),
        val isLoading: Boolean = true,
    ) : ViewState

    sealed interface Effect : ViewEffect {
        data object NavigateBack : Effect

        data object FetchFailedNotify : Effect

    }
}