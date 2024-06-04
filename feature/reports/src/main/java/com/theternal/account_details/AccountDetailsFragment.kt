package com.theternal.account_details

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import androidx.transition.Transition
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.reports.databinding.FragmentAccountDetailsBinding
import com.theternal.account_details.AccountDetailsContract.*
import com.theternal.common.extensions.format
import com.theternal.common.extensions.show
import com.theternal.core.base.Initializer
import com.theternal.uikit.adapters.RecordAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountDetailsFragment : BaseStatefulFragment<FragmentAccountDetailsBinding,
        AccountDetailsViewModel, Event, State, Effect>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentAccountDetailsBinding>
        get() = FragmentAccountDetailsBinding::inflate

    override val getViewModelClass: () -> Class<AccountDetailsViewModel> = {
        AccountDetailsViewModel::class.java
    }

    //! Screen Transitions
    override val transitionDuration: Long = 300
    override val viewEntering: Transition = Slide().apply {
        slideEdge = Gravity.RIGHT
    }

    //! UI Properties
    private val transferAdapter = RecordAdapter()

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentAccountDetailsBinding> = {
        postEvent(Event.GetAccount(arguments?.getLong("id")))
        transferList.adapter = transferAdapter

        goBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onStateUpdate(state: State) {
        if(state.account != null) {
            transferAdapter.submitList(state.transfers)
            binding {
                details.show()

                name.text = state.account.name

                if(state.account.note.isNullOrBlank()) {
                    note.text = "empty note"
                } else note.text = state.account.note

                balance.text = "${state.account.balance.format()} ${state.account.currency}"
            }
        }
    }

}