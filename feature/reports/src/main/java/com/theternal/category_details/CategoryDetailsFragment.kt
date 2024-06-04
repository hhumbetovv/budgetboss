package com.theternal.category_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import androidx.transition.Transition
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.reports.databinding.FragmentCategoryDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import com.theternal.category_details.CategoryDetailsContract.*
import com.theternal.common.extensions.format
import com.theternal.common.extensions.getColor
import com.theternal.core.base.interfaces.ViewEffect
import com.theternal.reports.R
import com.theternal.uikit.adapters.RecordAdapter
import com.theternal.common.R.color as Colors

@AndroidEntryPoint
class CategoryDetailsFragment : BaseStatefulFragment<FragmentCategoryDetailsBinding,
        CategoryDetailsViewModel, Event, State, ViewEffect.Empty>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentCategoryDetailsBinding>
        get() = FragmentCategoryDetailsBinding::inflate

    override val getViewModelClass: () -> Class<CategoryDetailsViewModel> = {
        CategoryDetailsViewModel::class.java
    }

    //! Screen Transitions
    override val transitionDuration: Long = 300
    override val viewEntering: Transition = Slide().apply {
        slideEdge = Gravity.RIGHT
    }

    //! UI Properties
    private val recordAdapter = RecordAdapter()
    private var isExpense = false
    private lateinit var prefix: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isExpense = arguments?.getBoolean("isExpense") ?: false
        prefix = if(isExpense) "-" else "+"
    }

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentCategoryDetailsBinding> = {
        goBackBtn.setOnClickListener { findNavController().popBackStack() }
        categoryLabel.text = arguments?.getString("category")
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
            binding.amount.text = "$prefix${state.totalAmount.format()} $"
        }
        recordAdapter.submitList(state.records)
    }
}