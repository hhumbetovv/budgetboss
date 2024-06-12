package com.theternal.account_details

import androidx.lifecycle.viewModelScope
import com.theternal.core.base.BaseViewModel
import com.theternal.account_details.AccountDetailsContract.*
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.usecases.DeleteAccountUseCase
import com.theternal.domain.usecases.GetAccountTransfersUseCase
import com.theternal.domain.usecases.GetAccountUseCase
import com.theternal.domain.usecases.GetCurrencyValueUseCase
import com.theternal.domain.usecases.UpdateAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val getCurrencyValueUseCase: GetCurrencyValueUseCase,
    private val getAccountTransfersUseCase: GetAccountTransfersUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase
) : BaseViewModel<Event, State, Effect>() {

    private var getAccountJob: Job? = null
    private var getTransfersJob: Job? = null

    override fun createState(): State {
        return State()
    }

    override fun onEventUpdate(event: Event) {
        when(event) {
            is Event.GetAccount -> getAccount(event.id)
            Event.EditAccount -> setState { state -> state.copy(editMode = true) }
            Event.DeleteAccount -> removeAccount()
            is Event.SaveAccount -> saveAccount(event.name, event.note, event.balance)
            Event.CancelEditAccount -> setState { state -> state.copy(editMode = false) }
        }
    }

    private fun getAccount(id: Long?) {
        if(id == null) return
        getAccountJob?.cancel()
        getAccountJob = getAccountUseCase(id).onEach { account ->
            setAccount(account)
        }.launchIn(viewModelScope)
    }

    private fun setAccount(account: AccountEntity) {
        if(currentState.currencyValue == null) {
            viewModelScope.launch(Dispatchers.IO) {
                val currencyValue = getCurrencyValueUseCase(account.currency)
                setState { state ->
                    state.copy(
                        account = account,
                        currencyValue = currencyValue,
                    )
                }
                getTransfers(account.transfers)
            }
        } else {
            setState { state ->
                state.copy(account = account)
            }
            getTransfers(account.transfers)
        }
    }

    private fun getTransfers(transfers: List<Long>) {
        getTransfersJob?.cancel()
        getTransfersJob = getAccountTransfersUseCase(transfers).onEach { transferRecords ->

            viewModelScope.launch(Dispatchers.IO) {
                val (incomes, expenses) = async {
                    transferRecords.partition {
                        it.senderId != currentState.account?.id
                    }
                }.await()

                setIncomes(incomes)

                setExpenses(expenses)
            }

        }.launchIn(viewModelScope)
    }

    private fun setIncomes(list: List<TransferEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            val totalIncomes = async {
                list.fold(BigDecimal.ZERO) { acc, record ->
                    acc + record.amount * record.exchangeValue
                }
            }.await()

            setState { state ->
                state.copy(
                    totalIncomes = totalIncomes,
                    incomeList = list.sortedByDescending { it.date }
                )
            }
        }
    }

    private fun setExpenses(list: List<TransferEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            val totalExpenses = async {
                list.fold(BigDecimal.ZERO) { acc, record ->
                    acc + record.amount
                }
            }.await()

            setState { state ->
                state.copy(
                    totalExpenses = totalExpenses,
                    expenseList = list.sortedByDescending { it.date }
                )
            }
        }
    }

    private fun removeAccount() {
        getAccountJob?.cancel()
        getTransfersJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {
            deleteAccountUseCase(
                account = currentState.account!!,
                records = currentState.incomeList + currentState.expenseList
            )
        }
        postEffect(Effect.NavigateBack)
    }

    private fun saveAccount(name: String, note: String, balance: BigDecimal?) {
        viewModelScope.launch(Dispatchers.IO) {
            updateAccountUseCase(currentState.account!!.copy(
                name = name.ifEmpty { "Empty" },
                note = note.ifEmpty { "Empty" },
                balance = balance ?: BigDecimal.ZERO
            ))
            setState { it.copy(editMode = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        getAccountJob?.cancel()
        getTransfersJob?.cancel()
    }
}