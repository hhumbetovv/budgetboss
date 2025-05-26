package com.theternal.domain.entities.aggregations // Or com.theternal.domain.entities

import java.math.BigDecimal

data class MonthlySummary(
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val totalExpense: BigDecimal = BigDecimal.ZERO
)
