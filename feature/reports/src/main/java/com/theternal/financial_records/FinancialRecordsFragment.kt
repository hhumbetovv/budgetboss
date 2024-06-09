package com.theternal.financial_records

import android.annotation.SuppressLint
import androidx.navigation.fragment.findNavController
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
import com.theternal.reports.databinding.FragmentFinancialRecordsBinding
import com.theternal.uikit.adapters.CategoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import com.theternal.financial_records.FinancialRecordsContract.*
import com.theternal.reports.ReportsFragmentDirections

@AndroidEntryPoint
class FinancialRecordsFragment : BaseStatefulFragment<FragmentFinancialRecordsBinding,
        FinancialRecordsViewModel, ViewEvent.Empty, State, ViewEffect.Empty>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentFinancialRecordsBinding>
        get() = FragmentFinancialRecordsBinding::inflate

    override val getViewModelClass: () -> Class<FinancialRecordsViewModel> = {
        FinancialRecordsViewModel::class.java
    }

    //! UI Properties
    private val incomeCategoryAdapter = CategoryAdapter { item ->
        navigateToDetails(item)
    }
    private val expenseCategoryAdapter = CategoryAdapter { item ->
        navigateToDetails(item)
    }

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentFinancialRecordsBinding> = {
        incomeCategoryList.adapter = incomeCategoryAdapter
        expenseCategoryList.adapter = expenseCategoryAdapter
    }

    @SuppressLint("SetTextI18n")
    override fun onStateUpdate(state: State) {
        state.apply {
            incomeCategoryAdapter.submitList(incomeCategories)
            expenseCategoryAdapter.submitList(expenseCategories)

            binding {
                incomeAmount.text = "+${totalIncome.format(true)}"
                expenseAmount.text = "-${totalExpense.format(true)}"

                if(totalIncome === BigDecimal.ZERO) {
                    incomeContainer.hide()
                } else incomeContainer.show()

                if(totalExpense === BigDecimal.ZERO) {
                    expenseContainer.hide()
                } else expenseContainer.show()

                if(state.incomeCategories.isEmpty() && state.expenseCategories.isEmpty()) {
                    emptyListTitle.show()
                } else {
                    emptyListTitle.hide()
                }
            }
        }
    }

    private fun navigateToDetails(item: CategoryAdapter.CategoryItem) {
        findNavController().navigate(
            ReportsFragmentDirections.toCategoryDetails(
                item.category.name.capitalize(),
                item.category is ExpenseCategory
            )
        )
    }
}