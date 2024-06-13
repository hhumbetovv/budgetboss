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
import com.theternal.common.constants.DOLLAR
import com.theternal.common.constants.MINUS
import com.theternal.common.constants.PLUS
import com.theternal.common.constants.TRANSFER_ARROW
import com.theternal.common.extensions.format
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
            setState { it.copy(accounts = accounts) }
        }.launchIn(viewModelScope)
    }

    override fun createState(): State {
        return State()
    }

    override fun onEventUpdate(event: Event) {
        when(event) {
            is Event.SetType -> {
                setState { it.copy(recordType = event.recordType) }
            }
            is Event.SetAmount -> setAmount(event.amount)
            is Event.SelectExpenseCategory -> {
                setState { it.copy(expenseCategory = event.category) }
            }
            is Event.SelectIncomeCategory -> {
                setState { it.copy(incomeCategory = event.category) }
            }
            is Event.SelectDate -> {
                setState { it.copy(date = event.date) }
            }
            is Event.SetReceiverAccount -> {
                setState { it.copy(transferTo = event.account) }
                exchange()
            }
            is Event.SetSenderAccount -> {
                setState { it.copy(transferFrom = event.account) }
                exchange()
            }
            Event.SwitchAccounts -> {
                setState{ state ->
                    state.copy(
                        transferTo = state.transferFrom,
                        transferFrom = state.transferTo
                    )
                }
                exchange()
            }
            is Event.CreateRecord -> {
                when(currentState.recordType) {
                    TRANSFER -> createTransferRecord(event.note)
                    else -> createFinancialRecord(event.note)
                }
            }
        }
    }

    private fun setAmount(amount: BigDecimal?) {
        setState { it.copy(amount = amount) }
        notifyJob?.cancel()
        notifyJob = viewModelScope.launch(Dispatchers.IO) {
            if(currentState.recordType == TRANSFER &&
                currentState.exchangeValue != null &&
                currentState.exchangeValue != BigDecimal.ONE) {
                delay(1000)
                postEffect(Effect.ExchangeNotify)
            }
        }
    }

    private fun exchange() {
        exchangeJob?.cancel()
        exchangeJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            setState { it.copy(isLoading = true, exchangeValue = null) }
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
                        setState { state ->
                            state.copy(
                                isLoading = false,
                                exchangeValue = value
                            )
                        }
                        postEffect(Effect.ExchangeNotify)
                    },
                    onError = {
                        setState { it.copy(isLoading = false) }
                        postEffect(Effect.CheckInternetNotify)
                    }
                )
            } else {
                setState { it.copy(isLoading = false) }
            }
        }
    }

    private fun createFinancialRecord(note: String?) {
        currentState.apply {
            val isExpense = recordType == EXPENSE
            val prefix = if(isExpense) MINUS else PLUS
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
                    amount = amount!!,
                    amountText = "$prefix${amount.format()} $DOLLAR"
                )
            )
//            Firebase.analytics.logEvent("create_record") {
//                param("record_type", recordType.name)
//                param("category", title)
//            }
        }
    }

    private fun createTransferRecord(note: String) {
        currentState.apply {
            createRecord(
                TransferEntity(
                    title = "${transferFrom!!.name} $TRANSFER_ARROW ${transferTo!!.name}",
                    amount = amount!!,
                    date = date,
                    note = note.ifEmpty { "Empty" },
                    senderId = transferFrom.id,
                    sentAmount = "$MINUS${amount.format()} ${transferFrom.currency}",
                    senderCurrency = transferFrom.currency,
                    receiverId = transferTo.id,
                    exchangeValue = exchangeValue!!,
                    receivedAmount = "$PLUS${(amount * exchangeValue).format()} ${transferFrom.currency}",
                    receiverCurrency = transferTo.currency
                )
            )
//            Firebase.analytics.logEvent(FirebaseEvents.CreateRecord) {
//                param("record_type", recordType.name)
//            }
        }
    }

    private fun createRecord(record: RecordEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            createRecordUseCase(record)
            postEffect(Effect.NavigateBack)
        }
    }
}