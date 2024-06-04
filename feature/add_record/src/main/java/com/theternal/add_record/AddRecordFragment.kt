package com.theternal.add_record

import android.view.Gravity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.transition.Slide
import androidx.transition.Transition
import com.google.android.material.datepicker.MaterialDatePicker
import com.theternal.uikit.adapters.CategoryAdapter
import com.theternal.common.extensions.capitalize
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Initializer
import com.theternal.domain.entities.base.ExpenseCategory
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.domain.entities.base.RecordType
import com.theternal.domain.entities.base.RecordType.*
import dagger.hilt.android.AndroidEntryPoint
import com.theternal.common.R.string as Strings
import com.theternal.common.R.color as Colors
import com.theternal.add_record.AddRecordContract.*
import com.theternal.add_record.databinding.FragmentAddRecordBinding
import com.theternal.common.extensions.format
import com.theternal.common.extensions.getColor
import com.theternal.common.extensions.hide
import com.theternal.common.extensions.setOnChangeListener
import com.theternal.common.extensions.setOnTabSelectedListener
import com.theternal.common.extensions.show
import com.theternal.common.extensions.showToast
import com.theternal.core.base.Inflater
import com.theternal.domain.entities.local.AccountEntity
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AddRecordFragment : BaseStatefulFragment<FragmentAddRecordBinding,
        AddRecordViewModel, Event, State, Effect>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentAddRecordBinding>
        get() = FragmentAddRecordBinding::inflate

    override val getViewModelClass: () -> Class<AddRecordViewModel> = {
        AddRecordViewModel::class.java
    }

    //! Screen Transitions
    override val transitionDuration: Long = 300
    override val viewEntering: Transition = Slide().apply {
        slideEdge = Gravity.BOTTOM
    }

    //! UI Properties
    private val categoryAdapter = CategoryAdapter { item ->
        postEvent(
            if(item.category is IncomeCategory) {
                Event.SelectIncomeCategory(item.category as IncomeCategory)
            } else {
                Event.SelectExpenseCategory(item.category as ExpenseCategory)
            }
        )
    }

    private val incomeItems = IncomeCategory.entries.map { category ->
        CategoryAdapter.CategoryItem(category)
    }

    private val expenseItems = ExpenseCategory.entries.map { category ->
        CategoryAdapter.CategoryItem(category)
    }

    private val bottomSheet = SelectAccountSheet()

    private val datePicker = MaterialDatePicker.Builder
        .datePicker()
        .setSelection(System.currentTimeMillis())
        .build()

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentAddRecordBinding> = {

        goBackBtn.setOnClickListener { findNavController().popBackStack() }

        initTabBar()

        initAmountField()

        initCategoryList()

        initAccounts()

        initBottomSheet()

        initDatePicker()

        date.text = SimpleDateFormat(
            "dd.MM.yyyy", Locale.getDefault()
        ).format(System.currentTimeMillis())

        saveBtn.setOnClickListener {
            postEvent(Event.CreateRecord(noteField.text.toString()))
        }
    }

    private fun initTabBar() {
        val tabBar = binding.tabBar
        RecordType.entries.forEach {
            tabBar.addTab(
                tabBar.newTab().apply {
                    text = it.name.capitalize()
                }
            )
        }
        tabBar.setOnTabSelectedListener {
            postEvent(Event.SetType(RecordType.entries[it]))
        }
    }

    private fun initAmountField() {
        binding.amountField.setOnChangeListener {
            postEvent(
                Event.SetAmount(it.toBigDecimalOrNull())
            )
        }
    }

    private fun initCategoryList() {
        val categoryList = binding.categoryList
        categoryList.adapter = categoryAdapter
        (categoryList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        categoryAdapter.submitList(incomeItems.map { item ->
            item.copy(isSelected = item.category == state?.incomeCategory)
        })
    }

    private fun initAccounts() {
        binding {
            senderAccount.setOnClickListener {
                bottomSheet.show(
                    parentFragmentManager,
                    SelectAccountSheet.AccountType.SENDER
                )
            }
            receiverAccount.setOnClickListener {
                bottomSheet.show(
                    parentFragmentManager,
                    SelectAccountSheet.AccountType.RECEIVER
                )
            }
            var isAnimating = false
            accountSwitch.setOnClickListener {
                if(!isAnimating) {
                    isAnimating = true
                    accountSwitch.animate().rotationBy(360f).setDuration(500).withEndAction {
                        isAnimating = false
                    }.start()
                    postEvent(Event.SwitchAccounts)
                }
            }
        }
    }

    private fun initBottomSheet() {
        bottomSheet.initialize { account, type ->
            when(type) {
                SelectAccountSheet.AccountType.SENDER -> {
                    postEvent(Event.SetSenderAccount(account))
                }
                SelectAccountSheet.AccountType.RECEIVER -> {
                    postEvent(Event.SetReceiverAccount(account))
                }
            }
        }
    }

    private fun initDatePicker() {
        datePicker.addOnPositiveButtonClickListener { selection ->
            postEvent(Event.SelectDate(selection))
        }
        binding.dateBtn.setOnClickListener {
            if(!datePicker.isAdded) {
                datePicker.show(parentFragmentManager, datePicker.tag)
            }
        }
    }

    //!  UI Updates
    override fun onStateUpdate(state: State) {
        updateAmountField()

        updateCategoryListVisibility(state.recordType != TRANSFER)

        updateCategoryList()

        updateAccounts()

        updateBottomSheet()

        binding.date.text = SimpleDateFormat(
            "dd.MM.yyyy", Locale.getDefault()
        ).format(state.date)

        updateSaveButton()

        if(state.isLoading) binding.loader.show() else binding.loader.hide()
    }

    private fun updateAmountField() {
        binding.amountField.hint = getString(
            when(state!!.recordType) {
                INCOME -> Strings.add_income
                EXPENSE -> Strings.add_expense
                TRANSFER -> Strings.add_amount
            }
        )
        binding.amountField.isEnabled = !state!!.isLoading
    }

    private fun updateCategoryListVisibility(isVisible: Boolean) {
        binding {
            if(isVisible) {
                categoryList.show()
                selectionTitle.show()
            } else {
                categoryList.hide()
                selectionTitle.hide()
            }
        }
    }

    private fun updateCategoryList() {
        if(state!!.recordType == TRANSFER) return
        state?.apply {
            val (list, selected) = when (recordType) {
                INCOME -> Pair(incomeItems, incomeCategory)
                else -> Pair(expenseItems, expenseCategory)
            }
            categoryAdapter.submitList(list.map { item ->
                item.copy(isSelected = item.category == selected)
            })
        }
    }

    private fun updateAccounts() {
        state?.apply {
            if(recordType == TRANSFER) {
                binding.accounts.show()
            } else {
                binding.accounts.hide()
            }
            updateSenderAccount(transferFrom)
            updateReceiverAccount(transferTo)
            binding.accountSwitch.isEnabled = !isLoading
        }
    }

    private fun updateBottomSheet() {
        state?.apply {
            bottomSheet.submitList(accounts.filter { account ->
                val isCurrentSender = account.id == transferFrom?.id
                val isCurrentReceiver = account.id == transferTo?.id
                !isCurrentSender && !isCurrentReceiver
            })
        }
    }

    private fun updateSenderAccount(account: AccountEntity?) {
        val text = account?.name ?: getString(Strings.select_account)
        val color = getColor(if(account == null) Colors.hint else Colors.danger)
        binding {
            sender.text = text
            sender.setTextColor(color)
            senderCurrency.text = account?.currency
        }
    }

    private fun updateReceiverAccount(account: AccountEntity?) {
        val text = account?.name ?: getString(Strings.select_account)
        val color = getColor(if(account == null) Colors.hint else Colors.primary)
        binding {
            receiver.text = text
            receiver.setTextColor(color)
            receiverCurrency.text = account?.currency
        }
    }

    private fun updateSaveButton() {
        state?.apply {
            binding.saveBtn.isEnabled = when(recordType) {
                INCOME -> incomeCategory != null
                EXPENSE -> expenseCategory != null
                TRANSFER -> transferTo != null && transferFrom !=  null && exchangeValue != null
            } && amount != null && amount != BigDecimal.ZERO && !isLoading
        }
    }

    //! UI Effects
    override fun onEffectUpdate(effect: Effect) {
        when(effect) {
            Effect.NavigateToMain -> findNavController().popBackStack()
            Effect.CheckInternetNotify -> {
                showToast("Please, check internet connection")
            }
            is Effect.ExchangeNotify -> {
                if (state?.amount == null || state?.amount == BigDecimal.ZERO) return
                state?.apply {
                    val from = "${amount!!.format()} ${transferFrom?.currency}"
                    val to = "${(amount * exchangeValue!!).format()} ${transferTo?.currency}"
                    showToast("$from is approximately $to")
                }
            }
        }
    }
}