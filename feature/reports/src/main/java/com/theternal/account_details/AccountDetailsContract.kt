package com.theternal.account_details

import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.core.base.interfaces.ViewState
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.entities.local.TransferEntity

sealed class AccountDetailsContract {

    sealed interface Event : ViewEvent {

        data class GetAccount(val id: Long?) : Event

    }

    data class State(
        val editMode: Boolean = false,
        val account: AccountEntity? = null,
        val transfers: List<TransferEntity> = listOf(),
        val newName: String = "",
    ) : ViewState

    sealed interface Effect : ViewEffect {

        data object NavigateBack : Effect

    }

}