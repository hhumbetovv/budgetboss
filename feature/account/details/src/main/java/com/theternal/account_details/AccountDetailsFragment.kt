package com.theternal.account_details

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import androidx.transition.Transition
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.SlideDistanceProvider
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.account_details.AccountDetailsContract.*
import com.theternal.account_details.databinding.FragmentAccountDetailsBinding
import com.theternal.common.constants.DEFAULT_CURRENCY
import com.theternal.common.constants.MINUS
import com.theternal.common.constants.PLUS
import com.theternal.common.constants.SLASH
import com.theternal.common.extensions.Colors
import com.theternal.common.extensions.Strings
import com.theternal.common.extensions.format
import com.theternal.common.extensions.getColor
import com.theternal.common.extensions.getDrawable
import com.theternal.common.extensions.hide
import com.theternal.common.extensions.show
import com.theternal.core.base.Initializer
import com.theternal.core.managers.ToolbarManager
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.record_details.RecordDetailsFragment
import com.theternal.uikit.adapters.RecordAdapter
import com.theternal.uikit.fragments.AppBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import com.theternal.uikit.R.drawable as Drawables

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
    private val viewTransition = MaterialFadeThrough().apply {
        secondaryAnimatorProvider = SlideDistanceProvider(Gravity.END)
    }
    override val transitionDuration: Long = 300
    override val viewEntering: Transition = viewTransition
    override val viewExiting: Transition = viewTransition

    //! UI Properties
    private val incomesAdapter = RecordAdapter { record -> showRecord(record) }
    private val expensesAdepter = RecordAdapter { record -> showRecord(record) }
    private var smile: Drawable? = null
    private var neutral: Drawable? = null
    private var frown: Drawable? = null

    private var primaryColor: Int? = null
    private var dangerColor: Int? = null
    private var textColor: Int? = null

    private lateinit var deleteDialog: AlertDialog.Builder
    private val recordDetailsFragment = RecordDetailsFragment()
    private val recordDetailsSheet = AppBottomSheetFragment { recordDetailsFragment }

    private fun showRecord(record: RecordEntity) {
        recordDetailsFragment.setProperties(record.id, record is TransferEntity)

        if(!recordDetailsSheet.isAdded) {
            recordDetailsSheet.show(childFragmentManager, "Record Details")
        }
    }

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentAccountDetailsBinding> = {
        postEvent(Event.GetAccount(arguments?.getLong("id")))

        initToolbar()

        initRecyclerViews()

        initDrawables()

        initColors()

        initActionButtons()

        initAlertDialog()
    }

    override fun onDestroyView() {
        smile = null
        neutral = null
        frown = null

        primaryColor = null
        dangerColor = null
        textColor = null

        super.onDestroyView()
    }

    private fun initToolbar() {
        (requireActivity() as ToolbarManager).apply {
            showSettingsIcon()
            showBackIcon()
            setTitle(getString(Strings.account_details))
        }
    }

    private fun initRecyclerViews() {
        binding.incomeList.adapter = incomesAdapter
        binding.expenseList.adapter = expensesAdepter
    }

    private fun initDrawables() {
        (binding.emoji.drawable as AnimatedVectorDrawable).start()
        smile = getDrawable(Drawables.ic_emoji_smile)
        neutral = getDrawable(Drawables.ic_emoji_neutral)
        frown = getDrawable(Drawables.ic_emoji_frown)
    }

    private fun initColors() {
        primaryColor = getColor(Colors.primary)
        dangerColor = getColor(Colors.danger)
        textColor = getColor(Colors.text)
    }

    private fun initActionButtons() {
        binding {
            positiveBtn.setOnClickListener {
                postEvent(
                    if(state?.editMode == true) {
                        Event.SaveAccount(
                            nameField.text.trim().toString(),
                            noteField.text.trim().toString(),
                            balanceField.text.toString().toBigDecimalOrNull()
                        )
                    } else {
                        Event.EditAccount
                    }
                )
            }

            negativeBtn.setOnClickListener {
                if(state?.editMode == true) {
                    postEvent(Event.CancelEditAccount)
                } else {
                    deleteDialog.show()
                }
            }
        }
    }

    private fun initAlertDialog() {
       deleteDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(Strings.delete_account))
            .setMessage(getString(Strings.account_delete_info))
            .setCancelable(true)
            .setPositiveButton(getString(Strings.delete)) { _, _ ->
                postEvent(Event.DeleteAccount)
            }
            .setNegativeButton(getString(Strings.cancel), null)
    }

    override fun onStateUpdate(state: State) {
        if(state.account != null) {

            updateAccountInfo(state.account, state.editMode)

            updateBalance(state.account.balance, state.account.currency, state.currencyValue)

            updateEmoji(state.account.balance)

            updateFields(state)

            updateActionButtons(state.editMode)

            updateIncomes(state.account.currency, state.totalIncomes, state.incomeList)

            updateExpenses(state.account.currency, state.totalExpenses, state.expenseList)

            if(state.incomeList.isEmpty() && state.expenseList.isEmpty()) {
                binding.emptyListTitle.show()
            } else {
                binding.emptyListTitle.hide()
            }
        }
    }

    private fun updateAccountInfo(account: AccountEntity, editMode: Boolean) {
        binding {
            details.show()

            val visibility = if(editMode) View.INVISIBLE else View.VISIBLE

            name.text = account.name
            name.visibility = visibility

            if(account.note.isNullOrBlank()) {
                note.text = getString(Strings.empty_note)
            } else note.text = account.note
            note.visibility = visibility

            balance.visibility = visibility
        }
    }

    private fun updateBalance(balance: BigDecimal, currency: String, currencyValue: BigDecimal?) {
        var accountBalance = "${balance.format()} $currency"
        if(currencyValue != null && (balance > BigDecimal.ZERO || balance < BigDecimal.ZERO)){
            accountBalance += " $SLASH ${(balance * currencyValue).format()} $DEFAULT_CURRENCY"
        }
        val color = when {
            balance > BigDecimal.ZERO -> primaryColor
            balance < BigDecimal.ZERO -> dangerColor
            else -> textColor
        } ?: getColor(Colors.text)
        binding.balance.text = accountBalance
        binding.balance.setTextColor(color)
        binding.balanceField.setTextColor(color)
    }

    private fun updateEmoji(balance: BigDecimal) {
        val drawable = when{
            balance > BigDecimal.ZERO -> smile
            balance < BigDecimal.ZERO -> frown
            else -> neutral
        }
        if(drawable != binding.emoji.drawable) {
            binding.emoji.setImageDrawable(drawable)
            (binding.emoji.drawable as Animatable).start()
        }
    }

    private fun updateFields(state: State) {
        binding {
            state.apply {
                nameField.setText(name.text)
                noteField.setText(note.text)
                balanceField.setText(account?.balance.toString())

                val visibility = if(editMode) View.VISIBLE else View.INVISIBLE
                nameField.visibility = visibility
                noteField.visibility = visibility
                balanceField.visibility = visibility
            }
        }
    }

    private fun updateActionButtons(editMode: Boolean) {
        binding {
            positiveBtn.setImageDrawable(getDrawable(
                if(editMode) Drawables.ic_check
                else Drawables.ic_edit
            ))
            negativeBtn.setImageDrawable(getDrawable(
                if(editMode) Drawables.ic_close
                else Drawables.ic_trash
            ))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateIncomes(
        currency: String,
        totalIncomes: BigDecimal?,
        list: List<TransferEntity>
    ) {
        binding {
            if(list.isEmpty()) {
                incomeContainer.visibility = View.GONE
            } else {
                incomeContainer.visibility = View.VISIBLE
            }
            if(totalIncomes != null) {
                incomes.text = "$PLUS${totalIncomes.format()} $currency"
            }
        }
        incomesAdapter.submitList(list)
    }

    @SuppressLint("SetTextI18n")
    private fun updateExpenses(
        currency: String,
        totalExpenses: BigDecimal?,
        list: List<TransferEntity>
    ) {
        binding {
            if(list.isEmpty()) {
                expenseContainer.visibility = View.GONE
            } else {
                expenseContainer.visibility = View.VISIBLE
            }
            if(totalExpenses != null) {
                expenses.text = "$MINUS${totalExpenses.format()} $currency"
            }
        }
        expensesAdepter.submitList(list)
    }

    override fun onEffectUpdate(effect: Effect) {
        when(effect) {
            Effect.NavigateBack -> {
                parentFragmentManager.popBackStack()
            }
        }
    }
}