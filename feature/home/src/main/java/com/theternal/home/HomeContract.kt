package com.theternal.home

import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.core.base.interfaces.ViewState
import com.theternal.domain.interfaces.RecordEntity
import java.math.BigDecimal

sealed class HomeContract {

    data class State(
        val balance: BigDecimal = BigDecimal.ZERO,
        val records: List<RecordEntity> = listOf()
    ) : ViewState

    sealed interface Effect : ViewEffect {

        data object FetchFailedNotify : Effect

    }

}