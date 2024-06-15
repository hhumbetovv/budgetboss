package com.theternal.categories

import com.theternal.core.base.interfaces.ViewState
import com.theternal.uikit.adapters.CategoryAdapter
import java.math.BigDecimal

sealed interface CategoriesContract {

    data class State(
        val incomeCategories: List<CategoryAdapter.CategoryItem> = listOf(),
        val expenseCategories: List<CategoryAdapter.CategoryItem> = listOf(),
        val totalIncome: BigDecimal = BigDecimal.ZERO,
        val totalExpense: BigDecimal = BigDecimal.ZERO
    ) : ViewState

}