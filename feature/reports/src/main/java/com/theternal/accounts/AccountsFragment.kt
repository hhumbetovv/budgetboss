package com.theternal.accounts

import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.reports.databinding.FragmentAccountsBinding
import dagger.hilt.android.AndroidEntryPoint
import com.theternal.accounts.AccountsContract.*
import com.theternal.add_account.AddAccountSheet
import com.theternal.core.base.Initializer
import com.theternal.core.base.interfaces.ViewEffect

@AndroidEntryPoint
class AccountsFragment : BaseStatefulFragment<FragmentAccountsBinding, AccountsViewModel,
        ViewEvent.Empty, State, ViewEffect.Empty>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentAccountsBinding>
        get() = FragmentAccountsBinding::inflate

    override val getViewModelClass: () -> Class<AccountsViewModel> = {
        AccountsViewModel::class.java
    }

    //! UI Properties
    private val accountAdapter = AccountAdapter()
    private val addAccountSheet = AddAccountSheet()


    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentAccountsBinding> = {
        accountList.adapter = accountAdapter
        addBtn.setOnClickListener {
            if(!addAccountSheet.isAdded) {
                addAccountSheet.show(parentFragmentManager, addAccountSheet.tag)
            }
        }
    }

    //!  UI Updates
    override fun onStateUpdate(state: State) {
        accountAdapter.submitList(state.accounts)
    }
}