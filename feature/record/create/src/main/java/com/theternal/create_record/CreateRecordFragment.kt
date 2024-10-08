package com.theternal.create_record

import android.view.Gravity
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import androidx.transition.Transition
import com.theternal.create_record.databinding.FragmentCreateRecordBinding
import com.theternal.uikit.adapters.CategoryAdapter
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Initializer
import com.theternal.domain.entities.base.ExpenseCategory
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.domain.entities.base.RecordType
import com.theternal.domain.entities.base.RecordType.*
import dagger.hilt.android.AndroidEntryPoint
import com.theternal.create_record.CreateRecordContract.*
import com.theternal.common.extensions.Strings
import com.theternal.common.extensions.format
import com.theternal.common.extensions.gone
import com.theternal.common.extensions.setOnChangeListener
import com.theternal.common.extensions.setOnTabSelectedListener
import com.theternal.common.extensions.show
import com.theternal.common.extensions.showToast
import com.theternal.core.base.Inflater
import com.theternal.core.managers.LayoutManager
import com.theternal.select_account.SelectAccountFragment
import com.theternal.uikit.fragments.AppBottomSheetFragment
import com.theternal.uikit.utility.getCategoryName
import java.math.BigDecimal

@AndroidEntryPoint
class CreateRecordFragment : BaseStatefulFragment<FragmentCreateRecordBinding,
        CreateRecordViewModel, Event, State, Effect>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentCreateRecordBinding>
        get() = FragmentCreateRecordBinding::inflate

    override val getViewModelClass: () -> Class<CreateRecordViewModel> = {
        CreateRecordViewModel::class.java
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

    private val selectAccountFragment = SelectAccountFragment()
    private val bottomSheet = AppBottomSheetFragment { selectAccountFragment }

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentCreateRecordBinding> = {

        initLayout()

        initTabBar()

        initAmountField()

        initCategoryList()

        initAccounts()

        initBottomSheet()

        initDateButton()

        saveBtn.setOnClickListener {
            postEvent(Event.CreateRecord(noteField.text.toString()))
        }
    }

    private fun initLayout() {
        (requireActivity() as LayoutManager).apply {
            showSettingsBtn()
            showBackBtn()
            hideNavBar()
            setTitle(getString(Strings.new_transaction))
        }
    }

    private fun initTabBar() {
        val tabBar = binding.tabBar
        RecordType.entries.forEach {
            tabBar.addTab(
                tabBar.newTab().apply {
                    text = getString(getCategoryName(it))
                }
            )
        }
        tabBar.setOnTabSelectedListener {
            postEvent(Event.SetType(RecordType.entries[it]))
        }

        val type = state?.recordType
        if(type != null) {
            val index = RecordType.entries.indexOf(type)
            tabBar.getTabAt(index)?.select()
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
        categoryList.itemAnimator = null
        categoryAdapter.submitList(incomeItems.map { item ->
            item.copy(isSelected = item.category == state?.incomeCategory)
        })
    }

    private fun initAccounts() {
        binding {
            senderAccount.setOnClickListener { showSelectAccountSheet(true) }
            receiverAccount.setOnClickListener { showSelectAccountSheet(false) }

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

    private fun initDateButton() {
        binding.dateBtn.setDate(
            state?.date ?: System.currentTimeMillis()
        )
        binding.dateBtn.setDateSelectionListener(parentFragmentManager) { date ->
            postEvent(Event.SelectDate(date))
        }
    }

    private fun showSelectAccountSheet(isSender: Boolean) {
        if(!bottomSheet.isAdded) {
            selectAccountFragment.setIsSender(isSender)
            bottomSheet.show(
                parentFragmentManager,
                selectAccountFragment.tag
            )
        }
    }

    private fun initBottomSheet() {
        selectAccountFragment.initialize { account, isSender ->
            postEvent(
                if(isSender) {
                    Event.SetSenderAccount(account)
                } else {
                    Event.SetReceiverAccount(account)
                }
            )
        }
    }

    //!  UI Updates
    override fun onStateUpdate(state: State) {

        updateAmountField()

        updateCategoryListVisibility(state.recordType != TRANSFER)

        updateCategoryList()

        updateAccounts()

        updateBottomSheet()

        updateSaveButton()

        if(state.isLoading) binding.loader.show() else binding.loader.gone()
    }

    private fun updateAmountField() {
        binding.amountField.hint = getString(
            when(state!!.recordType) {
                INCOME -> Strings.income
                EXPENSE -> Strings.expense
                TRANSFER -> Strings.amount
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
                categoryList.gone()
                selectionTitle.gone()
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
                binding.accountsContainer.show()
            } else {
                binding.accountsContainer.gone()
            }
            binding.senderAccount.setAccount(transferFrom)
            binding.receiverAccount.setAccount(transferTo)
            binding.accountSwitch.isEnabled = !isLoading
        }
    }

    private fun updateBottomSheet() {
        state?.apply {
            selectAccountFragment.submitList(accounts.filter { account ->
                val isCurrentSender = account.id == transferFrom?.id
                val isCurrentReceiver = account.id == transferTo?.id
                !isCurrentSender && !isCurrentReceiver
            })
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
            Effect.NavigateBack -> findNavController().navigateUp()
            Effect.CheckInternetNotify -> {
                showToast(getString(Strings.check_internet))
            }
            is Effect.ExchangeNotify -> {
                if (state?.amount == null || state?.amount == BigDecimal.ZERO) return
                state?.apply {
                    val from = "${amount!!.format()} ${transferFrom?.currency}"
                    val to = "${(amount * exchangeValue!!).format()} ${transferTo?.currency}"
                    showToast(getString(Strings.approximately, from, to))
                }
            }
        }
    }
}