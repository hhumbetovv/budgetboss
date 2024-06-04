package com.theternal.add_record

import androidx.lifecycle.viewModelScope
import com.theternal.common.extensions.capitalize
import com.theternal.core.base.BaseViewModel
import com.theternal.domain.entities.base.RecordType.*
import com.theternal.domain.usecases.CreateRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.theternal.add_record.AddRecordContract.*
import com.theternal.core.domain.NetworkRequest
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.domain.usecases.ExchangeUseCase
import com.theternal.domain.usecases.GetAllAccountsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.math.BigDecimal

@HiltViewModel
class AddRecordViewModel @Inject constructor(
    private val createRecordUseCase: CreateRecordUseCase,
    private val exchangeUseCase: ExchangeUseCase,
    getAllAccountsUseCase: GetAllAccountsUseCase
) : BaseViewModel<Event, State, Effect>() {

    private var exchangeJob: Job? = null
    private var notifyJob: Job? = null

    init {
        getAllAccountsUseCase().onEach { accounts ->
            setState(currentState.copy(
                accounts = accounts
            ))
        }.launchIn(viewModelScope)
    }

    override fun createState(): State {
        return State()
    }

    override fun onEventUpdate(event: Event) {
        when(event) {
            is Event.SetType -> {
                setState(currentState.copy(recordType = event.recordType))
            }
            is Event.SetAmount -> setAmount(event.amount)
            is Event.SelectExpenseCategory -> {
                setState(currentState.copy(expenseCategory = event.category))
            }
            is Event.SelectIncomeCategory -> {
                setState(currentState.copy(incomeCategory = event.category))
            }
            is Event.SelectDate -> {
                setState(currentState.copy(date = event.date))
            }
            is Event.SetReceiverAccount -> {
                setState(currentState.copy(transferTo = event.account))
                exchange()
            }
            is Event.SetSenderAccount -> {
                setState(currentState.copy(transferFrom = event.account))
                exchange()
            }
            Event.SwitchAccounts -> {
                setState(currentState.copy(
                    transferTo = currentState.transferFrom,
                    transferFrom = currentState.transferTo
                ))
                exchange()
            }
            is Event.CreateRecord -> {
                val note = if (event.note.isNullOrEmpty()) null else event.note
                when(currentState.recordType) {
                    TRANSFER -> createTransferRecord(note)
                    else -> createFinancialRecord(note)
                }
            }
        }
    }

    private fun setAmount(amount: BigDecimal?) {
        notifyJob?.cancel()
        notifyJob = viewModelScope.launch(Dispatchers.IO) {
            if(currentState.recordType == TRANSFER) delay(1000)
            setState(currentState.copy(amount = amount))
            if(currentState.exchangeValue != null && currentState.exchangeValue != BigDecimal.ONE) {
                postEffect(Effect.ExchangeNotify)
            }
        }
    }

    private fun exchange() {
        exchangeJob?.cancel()
        exchangeJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            setState(currentState.copy(isLoading = true, exchangeValue = null))
            if(currentState.transferFrom != null && currentState.transferTo != null) {
                makeRequest(
                    NetworkRequest.WithParams(
                        exchangeUseCase::invoke,
                        ExchangeUseCase.Params(
                            currentState.transferFrom!!.currency,
                            currentState.transferTo!!.currency,
                        ),
                    ),
                    onSuccess = { value ->
                        setState(currentState.copy(
                            isLoading = false,
                            exchangeValue = value
                        ))
                        postEffect(Effect.ExchangeNotify)
                    },
                    onError = {
                        setState(currentState.copy(isLoading = false))
                        postEffect(Effect.CheckInternetNotify)
                    }
                )
            } else {
                setState(currentState.copy(isLoading = false))
            }
        }
    }

    private fun createFinancialRecord(note: String?) {
        currentState.apply {
            val isExpense = recordType == EXPENSE
            val title = if(isExpense) {
                expenseCategory!!.name
            } else {
                incomeCategory!!.name
            }.capitalize()

            createRecord(
                FinancialRecordEntity(
                    title = title,
                    isExpense = isExpense,
                    date = date,
                    note = note,
                    amount = amount!!
                )
            )
        }
    }

    private fun createTransferRecord(note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            currentState.apply {
                createRecord(
                    TransferEntity(
                        title = "${transferFrom!!.name} ⇒ ${transferTo!!.name}",
                        amount = amount!!,
                        date = date,
                        note = note,
                        senderId = transferFrom.id,
                        senderCurrency = transferFrom.currency,
                        receiverId = transferTo.id,
                        exchangeValue = exchangeValue!!,
                        receiverCurrency = transferTo.currency
                    )
                )
            }
        }
    }

    private fun createRecord(record: RecordEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            createRecordUseCase(record)
            postEffect(Effect.NavigateToMain)
        }
    }
}