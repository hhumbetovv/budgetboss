package com.theternal.account_details

import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.core.base.interfaces.ViewState
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.entities.local.TransferEntity
import java.math.BigDecimal

sealed interface AccountDetailsContract {

    sealed interface Event : ViewEvent {

        data class GetAccount(val id: Long?) : Event

        data object EditAccount : Event

        data object DeleteAccount : Event

        data class SaveAccount(
            val name: String,
            val note: String,
            val balance: BigDecimal?
        ) : Event

        data object CancelEditAccount : Event
    }

    data class State(
        val editMode: Boolean = false,
        val account: AccountEntity? = null,
        val currencyValue: BigDecimal? = null,
        val incomeList: List<TransferEntity> = listOf(),
        val expenseList: List<TransferEntity> = listOf(),
        val totalIncomes: BigDecimal? = null,
        val totalExpenses: BigDecimal? = null,
        val newName: String = "",
    ) : ViewState

    sealed interface Effect : ViewEffect {

        data object NavigateBack : Effect

    }

}