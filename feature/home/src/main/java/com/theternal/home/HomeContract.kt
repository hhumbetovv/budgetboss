package com.theternal.home

import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.core.base.interfaces.ViewState
import com.theternal.domain.entities.aggregations.MonthlySummary // Ensure correct import
import com.theternal.domain.interfaces.RecordEntity
import java.math.BigDecimal

sealed interface HomeContract {

    data class State(
        val balance: BigDecimal = BigDecimal.ZERO,
        val records: List<RecordEntity> = listOf(),
        val monthlySummary: MonthlySummary = MonthlySummary() // Added
    ) : ViewState

    sealed interface Effect : ViewEffect {

        data object FetchFailedNotify : Effect

    }

}