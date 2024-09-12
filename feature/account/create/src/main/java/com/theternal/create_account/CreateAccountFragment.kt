package com.theternal.create_account

import android.view.View
import android.widget.ArrayAdapter
import com.theternal.create_account.databinding.FragmentCreateAccountBinding
import com.theternal.common.extensions.setOnChangeListener
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.create_account.CreateAccountContract.*
import com.theternal.common.extensions.Strings
import com.theternal.common.extensions.hide
import com.theternal.common.extensions.setOnItemSelectedListener
import com.theternal.common.extensions.show
import com.theternal.common.extensions.showToast
import com.theternal.uikit.fragments.AppBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFragment : BaseStatefulFragment<FragmentCreateAccountBinding,
        CreateAccountViewModel, Event, State, Effect>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentCreateAccountBinding>
        get() = FragmentCreateAccountBinding::inflate

    override val getViewModelClass: () -> Class<CreateAccountViewModel> = {
        CreateAccountViewModel::class.java
    }

    //! UI Properties
    private var list: List<String> = listOf()

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentCreateAccountBinding> = {

        (parentFragment as AppBottomSheetFragment).setTitle(
            getString(Strings.create_account)
        )

        nameField.setOnChangeListener {
            postEvent(Event.SetAccountName(it))
        }

        balanceField.setOnChangeListener {
            postEvent(Event.SetBalance(it.toBigDecimalOrNull()))
        }

        setSpinnerItems(listOf())

        currencyList.setOnItemSelectedListener {
            postEvent(Event.SetCurrency(it))
        }

        saveBtn.setOnClickListener {
            postEvent(Event.CreateAccount(noteField.text.toString()))
        }

    }

    private fun setSpinnerItems(list: List<String>) {
        if(this.list == list) return
        this.list = list
        val currencyListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            list
        )
        currencyListAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.currencyList.adapter = currencyListAdapter
    }

    //!  UI Updates
    override fun onStateUpdate(state: State) {
        binding {
            setSpinnerItems(state.currencyList)

            if(state.isLoading) loader.show() else loader.hide()

            listOf(currencyList, nameField, noteField, balanceField).forEach { view ->
                view.isEnabled = !state.isLoading
            }

            saveBtn.isEnabled = state.accountName.isNotEmpty()
                    && !state.isLoading
                    && state.currency != null
        }
    }

    override fun onEffectUpdate(effect: Effect) {
        when(effect) {
            Effect.NavigateBack -> {
                (parentFragment as AppBottomSheetFragment).dismiss()
            }
            Effect.FetchFailedNotify -> {
                showToast(getString(Strings.currency_fetch_failed))
            }
        }
    }
}