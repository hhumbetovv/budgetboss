package com.theternal.add_record

import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.core.base.interfaces.ViewState
import com.theternal.domain.entities.base.ExpenseCategory
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.domain.entities.base.RecordType
import com.theternal.domain.entities.local.AccountEntity
import java.math.BigDecimal


sealed class AddRecordContract {

    sealed interface Event : ViewEvent {

        data class SetType(val recordType: RecordType) : Event

        data class SetAmount(val amount: BigDecimal?) : Event

        data class SelectIncomeCategory(val category: IncomeCategory) : Event

        data class SelectExpenseCategory(val category: ExpenseCategory) : Event

        data class SelectDate(val date: Long) : Event

        data class SetSenderAccount(val account: AccountEntity): Event

        data class SetReceiverAccount(val account: AccountEntity): Event

        data object SwitchAccounts : Event

        data class CreateRecord(val note: String? = null) : Event

    }

    data class State(
        val recordType: RecordType = RecordType.INCOME,
        val date: Long = System.currentTimeMillis(),
        val incomeCategory: IncomeCategory? = null,
        val expenseCategory: ExpenseCategory? = null,
        val transferFrom: AccountEntity? = null,
        val transferTo: AccountEntity? = null,
        val amount: BigDecimal? = null,
        val accounts: List<AccountEntity> = listOf(),
        val isLoading: Boolean = false,
        val exchangeValue: BigDecimal? = null,
    ) : ViewState

    sealed interface Effect : ViewEffect {

        data object NavigateToMain : Effect

        data object CheckInternetNotify : Effect

        data object ExchangeNotify : Effect

    }
}