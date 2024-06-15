package com.theternal.categories

import androidx.lifecycle.viewModelScope
import com.theternal.core.base.BaseViewModel
import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.domain.entities.base.ExpenseCategory
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.usecases.GetAllExpensesUseCase
import com.theternal.domain.usecases.GetAllIncomesUseCase
import com.theternal.uikit.adapters.CategoryAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.math.BigDecimal
import javax.inject.Inject
import com.theternal.categories.CategoriesContract.*

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getAllIncomesUseCase: GetAllIncomesUseCase,
    private val getAllExpensesUseCase: GetAllExpensesUseCase
) : BaseViewModel<ViewEvent.Empty, State, ViewEffect.Empty>() {

    override fun createState(): State {
        return State()
    }

    init { getAllRecords() }

    private fun getAllRecords() {
        combine(getAllIncomesUseCase(), getAllExpensesUseCase()) { incomes, expenses ->

            val (incomeCategories, totalIncome) = getCategoryList<IncomeCategory>(incomes)

            val (expenseCategories, totalExpense) = getCategoryList<ExpenseCategory>(expenses)

            State(incomeCategories, expenseCategories, totalIncome, totalExpense)
        }.onEach { state ->
            setState { state }
        }.launchIn(viewModelScope)
    }

    private inline fun <reified T: Enum<*>> getCategoryList(
        list: List<FinancialRecordEntity>
    ): Pair<List<CategoryAdapter.CategoryItem>, BigDecimal> {

        val categories =  list.sortedByDescending { it.date }.fold(
            mutableMapOf<Enum<*>, BigDecimal>()
        ) { acc, record ->

            val category = with(record.title.uppercase()) {
                if(T::class == IncomeCategory::class) {
                    IncomeCategory.valueOf(this)
                } else {
                    ExpenseCategory.valueOf(this)
                }
            }

            acc[category] = (acc[category] ?: BigDecimal.ZERO) + record.amount

            acc
        }.map { (category, amount) -> CategoryAdapter.CategoryItem(
            category = category,
            amount = amount
        ) }

        val total = categories.fold(BigDecimal.ZERO) { acc, item ->
            acc + (item.amount ?: BigDecimal.ZERO)
        }

        return Pair(categories, total)
    }
}