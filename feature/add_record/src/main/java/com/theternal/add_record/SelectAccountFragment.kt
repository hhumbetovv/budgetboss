package com.theternal.add_record

import com.theternal.add_account.AddAccountFragment
import com.theternal.add_record.databinding.FragmentSelectAccountBinding
import com.theternal.core.base.BaseStatelessFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.uikit.fragments.AppBottomSheetFragment

class SelectAccountFragment : BaseStatelessFragment<FragmentSelectAccountBinding>() {

    private lateinit var onItemClickListener: (AccountEntity, Boolean) -> Unit
    private var isSender: Boolean? = null

    private var listener: (AccountEntity) -> Unit = { account ->
        onItemClickListener(account, isSender ?: true)
        (parentFragment as AppBottomSheetFragment).dismiss()
    }

    private val selectAccountAdapter = SelectAccountAdapter(listener)
    private val addAccountSheet = AppBottomSheetFragment { AddAccountFragment() }

    fun initialize(
        onItemClickListener: (AccountEntity, Boolean) -> Unit,
    ) {
        this.onItemClickListener = onItemClickListener
    }

    fun setIsSender(isSender: Boolean) {
        this.isSender = isSender
    }

    fun submitList(
        accounts: List<AccountEntity>
    ) {
        selectAccountAdapter.submitList(accounts)
    }

    override val inflateBinding: Inflater<FragmentSelectAccountBinding>
        get() = FragmentSelectAccountBinding::inflate


    override val initViews: Initializer<FragmentSelectAccountBinding> = {
        accountList.adapter = selectAccountAdapter

        (parentFragment as AppBottomSheetFragment).setTitle(
            if(isSender != false) "Transfer From"
            else "Transfer To"
        )

        addBtn.setOnClickListener {
            if(!addAccountSheet.isAdded) {
                addAccountSheet.show(parentFragmentManager, addAccountSheet.tag)
            }
        }
    }
}