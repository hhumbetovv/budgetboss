package com.theternal.record_details

import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.core.base.interfaces.ViewState
import com.theternal.domain.interfaces.RecordEntity

sealed interface RecordDetailsContract {

    sealed interface Event : ViewEvent {

        data class GetRecord(val id: Long, val isTransfer: Boolean) : Event

        data class SetNote(val note: String? = null) : Event

        data class SelectDate(val date: Long) : Event

        data object DeleteRecord : Event

        data object UpdateRecord : Event

    }

    data class State(
        val note: String? = null,
        val date: Long? = null,
        val record: RecordEntity? = null
    ) : ViewState

    sealed interface Effect : ViewEffect {

        data object NavigateBack : Effect

    }

}