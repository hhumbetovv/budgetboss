package com.theternal.add_account

import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import com.theternal.core.base.BaseViewModel
import com.theternal.add_account.AddAccountContract.*
import com.theternal.core.domain.NetworkRequest
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.usecases.CreateAccountUseCase
import com.theternal.domain.usecases.ExchangeUseCase
import com.theternal.domain.usecases.FetchCurrencyListUseCase
import com.theternal.domain.usecases.GetLocalCurrencyListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val fetchCurrencyListUseCase: FetchCurrencyListUseCase,
    private val getLocalCurrencyListUseCase: GetLocalCurrencyListUseCase,
    private val exchangeUseCase: ExchangeUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
) : BaseViewModel<Event, State, Effect>() {

    override fun createState(): State {
        return State()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            makeRequest(
                NetworkRequest.NoParams(fetchCurrencyListUseCase::invoke),
                onSuccess = { list: List<String> ->
                    setState { it.copy(currencyList = list, isLoading = false) }
                },
                onError = {
                   getLocalCurrencyList()
                },
            )
        }
    }

    private fun getLocalCurrencyList() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { state ->
                state.copy(
                    currencyList = getLocalCurrencyListUseCase(),
                    isLoading = false
                )
            }
        }
    }

    override fun onEventUpdate(event: Event) {
        when(event) {
            is Event.SetAccountName -> {
                setState { it.copy(accountName = event.accountName) }
            }
            is Event.SetBalance -> {
                setState { it.copy(balance = event.balance) }
            }
            is Event.SetCurrency -> {
                setState { it.copy(currency = event.currency) }
            }
            is Event.CreateAccount -> {
                fetchCurrencyAndCreateAccount(event.note)
            }
        }
    }

    private fun fetchCurrencyAndCreateAccount(note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            setState { it.copy(isLoading = true) }
            makeRequest(
                NetworkRequest.WithParams(
                    exchangeUseCase::invoke,
                    ExchangeUseCase.Params(currentState.currency!!)
                ),
                onSuccess = { currencyValue -> createAccount(note, currencyValue) },
                onError = { getLocalCurrencyAndCreateAccount(note) },
            )
            setState { it.copy(isLoading = false) }
        }
    }

    private fun getLocalCurrencyAndCreateAccount(note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if(getLocalCurrencyListUseCase().contains(currentState.currency!!)) {
                createAccount(note)
            } else {
                postEffect(Effect.FetchFailedNotify)
            }
        }
    }

    private fun createAccount(note: String?, currencyValue: BigDecimal? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            createAccountUseCase(
                account = AccountEntity(
                    name = currentState.accountName,
                    balance = currentState.balance ?: BigDecimal.ZERO,
                    currency = currentState.currency!!,
                    note = note
                ),
                currencyValue = currencyValue
            )
            Firebase.analytics.logEvent("create_account") {
                param("currency_name", currentState.currency!!)
            }
            postEffect(Effect.NavigateBack)
        }
    }
}