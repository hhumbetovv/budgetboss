package com.theternal.category_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import androidx.navigation.fragment.findNavController
import androidx.transition.Transition
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.SlideDistanceProvider
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.reports.databinding.FragmentCategoryDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import com.theternal.category_details.CategoryDetailsContract.*
import com.theternal.common.extensions.format
import com.theternal.common.extensions.getColor
import com.theternal.record_details.RecordAdapter
import com.theternal.common.R.color as Colors

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
    private var recordAdapter: RecordAdapter? = null
    private var isExpense = false
    private lateinit var prefix: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isExpense = arguments?.getBoolean("isExpense") ?: false
        prefix = if(isExpense) "-" else "+"
    }

    override fun onDestroyView() {
        recordAdapter = null
        super.onDestroyView()
    }

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentCategoryDetailsBinding> = {
        goBackBtn.setOnClickListener { findNavController().popBackStack() }
        categoryLabel.text = arguments?.getString("category")

        recordAdapter = RecordAdapter(childFragmentManager)
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
        recordAdapter?.submitList(state.records)
    }

    override fun onEffectUpdate(effect: Effect) {
        when(effect) {
            Effect.NavigateBack -> findNavController().popBackStack()
        }
    }
}