package com.theternal.add_record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theternal.add_account.AddAccountSheet
import com.theternal.add_record.adapters.SelectAccountAdapter
import com.theternal.add_record.databinding.SheetSelectAccountBinding
import com.theternal.domain.entities.local.AccountEntity

class SelectAccountSheet : BottomSheetDialogFragment() {

    enum class AccountType(val label: String) {
        SENDER("Transfer From"),
        RECEIVER("Transfer To")
    }

    private lateinit var onItemClickListener: (AccountEntity, AccountType) -> Unit
    private var binding: SheetSelectAccountBinding? = null
    private var type = AccountType.SENDER
    private var listener: (AccountEntity) -> Unit = { account ->
        onItemClickListener(account, type)
        dismiss()
    }
    private val selectAccountAdapter = SelectAccountAdapter(listener)
    private val addAccountSheet = AddAccountSheet()

    fun initialize(
        onItemClickListener: (AccountEntity, AccountType) -> Unit
    ) {
        this.onItemClickListener = onItemClickListener
    }

    fun submitList(
        accounts: List<AccountEntity>
    ) {
        selectAccountAdapter.submitList(accounts)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SheetSelectAccountBinding.inflate(inflater, container, false)
        return binding?.root
    }

    fun show(manager: FragmentManager, type: AccountType) {
        this.type = type
        super.show(manager, tag)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            accountList.adapter = selectAccountAdapter
            title.text = type.label
            addBtn.setOnClickListener {
                if(!addAccountSheet.isAdded) {
                    addAccountSheet.show(parentFragmentManager, addAccountSheet.tag)
                }
            }
            closeBtn.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}