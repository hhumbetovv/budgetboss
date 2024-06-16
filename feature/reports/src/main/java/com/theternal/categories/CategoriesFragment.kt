package com.theternal.categories

import android.annotation.SuppressLint
import androidx.navigation.fragment.findNavController
import com.theternal.common.constants.MINUS
import com.theternal.common.constants.PLUS
import com.theternal.common.extensions.capitalize
import com.theternal.common.extensions.format
import com.theternal.common.extensions.hide
import com.theternal.common.extensions.show
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.domain.entities.base.ExpenseCategory
import com.theternal.uikit.adapters.CategoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.theternal.categories.CategoriesContract.*
import com.theternal.common.extensions.safeNavigate
import com.theternal.reports.ReportsFragmentDirections
import com.theternal.reports.databinding.FragmentCategoriesBinding

@AndroidEntryPoint
class CategoriesFragment : BaseStatefulFragment<FragmentCategoriesBinding,
        CategoriesViewModel, ViewEvent.Empty, State, ViewEffect.Empty>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentCategoriesBinding>
        get() = FragmentCategoriesBinding::inflate

    override val getViewModelClass: () -> Class<CategoriesViewModel> = {
        CategoriesViewModel::class.java
    }

    //! UI Properties
    private val incomeCategoryAdapter = CategoryAdapter { category ->
        navigateToDetails(category)
    }
    private val expenseCategoryAdapter = CategoryAdapter { category ->
        navigateToDetails(category)
    }

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentCategoriesBinding> = {
        incomeCategoryList.adapter = incomeCategoryAdapter
        expenseCategoryList.adapter = expenseCategoryAdapter
    }

    @SuppressLint("SetTextI18n")
    override fun onStateUpdate(state: State) {
        state.apply {
            incomeCategoryAdapter.submitList(incomeCategories)
            expenseCategoryAdapter.submitList(expenseCategories)

            binding {
                totalIncomes.text = "$PLUS${totalIncome.format(true)}"
                totalExpenses.text = "$MINUS${totalExpense.format(true)}"

                if(state.incomeCategories.isEmpty() && state.expenseCategories.isEmpty()) {
                    incomeContainer.hide(false)
                    expenseContainer.hide(false)
                    emptyListTitle.show(false)
                } else {
                    incomeContainer.show(false)
                    expenseContainer.show(false)
                    emptyListTitle.hide(false)
                }
            }
        }
    }

    private fun navigateToDetails(category: CategoryAdapter.CategoryItem) {
        findNavController().safeNavigate(
            ReportsFragmentDirections.toCategoryDetails(
                category.category.name.capitalize(),
                category.category is ExpenseCategory
            )
        )
    }
}