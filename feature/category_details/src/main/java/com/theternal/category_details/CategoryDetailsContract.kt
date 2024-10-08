package com.theternal.category_details

import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.core.base.interfaces.ViewState
import com.theternal.domain.entities.local.FinancialRecordEntity
import java.math.BigDecimal

sealed interface CategoryDetailsContract {

    sealed interface Event : ViewEvent  {

        data class GetRecords(val title: String?, val isExpense: Boolean) : Event

    }

    data class State(
        val totalAmount: BigDecimal? = null,
        val records: List<FinancialRecordEntity> = listOf()
    ) : ViewState

    sealed interface Effect : ViewEffect {

        data object NavigateBack : Effect

    }
}