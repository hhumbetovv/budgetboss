package com.theternal.category_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import androidx.transition.Transition
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.SlideDistanceProvider
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import dagger.hilt.android.AndroidEntryPoint
import com.theternal.category_details.CategoryDetailsContract.*
import com.theternal.category_details.databinding.FragmentCategoryDetailsBinding
import com.theternal.common.constants.MINUS
import com.theternal.common.constants.PLUS
import com.theternal.common.extensions.Colors
import com.theternal.common.extensions.Strings
import com.theternal.common.extensions.format
import com.theternal.common.extensions.getColor
import com.theternal.core.managers.ToolbarManager
import com.theternal.domain.entities.base.ExpenseCategory
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.record_details.RecordDetailsFragment
import com.theternal.uikit.adapters.RecordAdapter
import com.theternal.uikit.fragments.AppBottomSheetFragment
import com.theternal.uikit.utility.getCategoryName

@AndroidEntryPoint
class CategoryDetailsFragment : BaseStatefulFragment<FragmentCategoryDetailsBinding,
        CategoryDetailsViewModel, Event, State, Effect>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentCategoryDetailsBinding>
        get() = FragmentCategoryDetailsBinding::inflate

    override val getViewModelClass: () -> Class<CategoryDetailsViewModel> = {
        CategoryDetailsViewModel::class.java
    }

    //! Screen Transitions
    private val viewTransition = MaterialFadeThrough().apply {
        secondaryAnimatorProvider = SlideDistanceProvider(Gravity.END)
    }
    override val transitionDuration: Long = 300
    override val viewEntering: Transition = viewTransition
    override val viewExiting: Transition = viewTransition

    //! UI Properties
    private val recordDetailsFragment = RecordDetailsFragment()
    private val recordDetailsSheet = AppBottomSheetFragment { recordDetailsFragment }

    private var recordAdapter = RecordAdapter { record ->
        recordDetailsFragment.setProperties(record.id, record is TransferEntity)

        if(!recordDetailsSheet.isAdded) {
            recordDetailsSheet.show(childFragmentManager, "Record Details")
        }
    }

    private var isExpense = false
    private lateinit var prefix: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isExpense = arguments?.getBoolean("isExpense") ?: false
        prefix = if(isExpense) MINUS else PLUS
    }

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentCategoryDetailsBinding> = {

        (requireActivity() as ToolbarManager).apply {
            showBackIcon()
            setTitle(getString(Strings.category_details))
        }

        categoryLabel.text = getString(getCategoryName(
            arguments?.getString("category")?.uppercase().let { label ->
                if(isExpense) ExpenseCategory.valueOf(label ?: "OTHERS")
                else IncomeCategory.valueOf(label ?: "OTHERS")
            }
        ))

        recordList.adapter = recordAdapter

        amount.setTextColor(
            getColor(
                if(isExpense) Colors.danger
                else Colors.primary
            )
        )

        postEvent(Event.GetRecords(
            title = arguments?.getString("category"),
            isExpense = isExpense,
        ))

    }

    @SuppressLint("SetTextI18n")
    override fun onStateUpdate(state: State) {
        if(state.totalAmount != null) {
            binding.amount.text = prefix + state.totalAmount.format(true)
        }
        recordAdapter.submitList(state.records)
    }

    override fun onEffectUpdate(effect: Effect) {
        when(effect) {
            Effect.NavigateBack -> {
                parentFragmentManager.popBackStack()
            }
        }
    }
}