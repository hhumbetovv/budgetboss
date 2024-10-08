package com.theternal.record_details

import android.app.AlertDialog
import com.theternal.common.extensions.Colors
import com.theternal.common.extensions.Strings
import com.theternal.common.extensions.getColor
import com.theternal.common.extensions.getDrawable
import com.theternal.common.extensions.setOnChangeListener
import com.theternal.record_details.RecordDetailsContract.*
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.domain.entities.base.ExpenseCategory
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.domain.entities.base.RecordType
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.record_details.databinding.FragmentRecordDetailsBinding
import com.theternal.uikit.fragments.AppBottomSheetFragment
import com.theternal.uikit.utility.getCategoryIcon
import com.theternal.uikit.utility.getCategoryName
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordDetailsFragment : BaseStatefulFragment<FragmentRecordDetailsBinding,
        RecordDetailsViewModel, Event, State, Effect>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentRecordDetailsBinding>
        get() = FragmentRecordDetailsBinding::inflate

    override val getViewModelClass: () -> Class<RecordDetailsViewModel> = {
        RecordDetailsViewModel::class.java
    }

    //! UI Properties
    private lateinit var deleteDialog: AlertDialog.Builder
    private var id: Long? = null
    private var isTransfer: Boolean? = null

    fun setProperties(id: Long, isTransfer: Boolean) {
        this.id = id
        this.isTransfer = isTransfer
    }

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentRecordDetailsBinding> = {

        if(id != null && isTransfer != null) {
            postEvent(Event.GetRecord(id!!, isTransfer!!))
        }

        (parentFragment as AppBottomSheetFragment).setTitle(
            getString(
                if(isTransfer != false) Strings.transfer_record
                else Strings.financial_record
            )
        )

        noteField.setOnChangeListener {
            postEvent(Event.SetNote(it.trim()))
        }

        dateBtn.setDateSelectionListener(parentFragmentManager) { date ->
            postEvent(Event.SelectDate(date))
        }

        saveBtn.setOnClickListener { postEvent(Event.UpdateRecord) }
        deleteBtn.setOnClickListener { deleteDialog.show() }

        deleteDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(Strings.delete_record))
            .setMessage(
                getString(
                    if(isTransfer != false) Strings.transfer_info
                    else Strings.record_info
                )
            )
            .setCancelable(true)
            .setPositiveButton(getString(Strings.delete)) { _, _ ->
                postEvent(Event.DeleteRecord)
            }
            .setNegativeButton(getString(Strings.cancel), null)
    }

    //! UI Updates
    override fun onStateUpdate(state: State) {

        state.apply {
            updateIcon(record)

            updateTitle(state.record)

            updateAmount(record)

            updateNote(record?.note, note)

            updateDate(record?.date, date)

            binding.saveBtn.isEnabled = note != record?.note ||
                    (date != record?.date && date != null)
        }
    }

    private fun updateTitle(record: RecordEntity?) {
        if(record == null) return
        if(record is TransferEntity) {
            binding.title.text = record.title
        } else {
            val isExpense = (record as FinancialRecordEntity).isExpense
            binding.title.text = getString(getCategoryName(
                if(isExpense) ExpenseCategory.valueOf(record.title.uppercase())
                else IncomeCategory.valueOf(record.title.uppercase())
            ))
        }

    }


    private fun updateIcon(record: RecordEntity?) {
        if(record == null) return
        binding.icon.setImageDrawable(
            getDrawable(
                getCategoryIcon(
                    when {
                        record is TransferEntity -> RecordType.TRANSFER
                        (record as FinancialRecordEntity).isExpense -> {
                            ExpenseCategory.valueOf(record.title.uppercase())
                        }
                        else -> IncomeCategory.valueOf(record.title.uppercase())
                    }
                )
            )
        )
    }

    private fun updateAmount(record: RecordEntity?) {
        if(record == null) return
        if(record is TransferEntity) {
            setTransferAmounts(record)
        } else {
            setAmount((record as FinancialRecordEntity).isExpense, record.amountText)
        }
    }

    private fun setTransferAmounts(record: TransferEntity) {
        binding.sent.text = record.sentAmount
        binding.received.text = record.receivedAmount
    }

    private fun setAmount(isExpense: Boolean, amount: String) {
        binding.amount.text = amount
        val color = if(isExpense) Colors.danger else Colors.primary
        binding.amount.setTextColor(getColor(color))
    }

    private fun updateNote(recordNote: String?, note: String?) {
        if(note == null && recordNote != null) {
            binding.noteField.setText(
                recordNote
            )
        }
    }

    private fun updateDate(recordDate: Long?, date: Long?) {
        if(date == null && recordDate != null) {
            binding.dateBtn.setDate(recordDate)
        }
    }

    override fun onEffectUpdate(effect: Effect) {
        when(effect) {
            Effect.NavigateBack -> (parentFragment as AppBottomSheetFragment).dismiss()
        }
    }
}