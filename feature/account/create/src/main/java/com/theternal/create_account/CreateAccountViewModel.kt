package com.theternal.create_account

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.theternal.core.base.BaseViewModel
import com.theternal.create_account.CreateAccountContract.*
import com.theternal.core.domain.NetworkRequest
import com.theternal.domain.entities.local.AccountEntity
import com.theternal.domain.usecases.CreateAccountUseCase
import com.theternal.domain.usecases.ExchangeUseCase
import com.theternal.domain.usecases.FetchCodesUseCase
import com.theternal.domain.usecases.GetLocalCurrencyListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val fetchCodesUseCase: FetchCodesUseCase,
    private val getLocalCurrencyListUseCase: GetLocalCurrencyListUseCase,
    private val exchangeUseCase: ExchangeUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
) : BaseViewModel<Event, State, Effect>() {

    override fun createState(): State {
        return State()
    }

    init { fetchCurrencyList() }

    private fun fetchCurrencyList() {
        viewModelScope.launch(Dispatchers.IO) {
            makeRequest(
                NetworkRequest.NoParams(fetchCodesUseCase::invoke),
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
            val currencyList = getLocalCurrencyListUseCase()
            setState { state ->
                state.copy(
                    currencyList = currencyList,
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
                onSuccess = { currencyValue ->
                    createAccount(note, currencyValue)
                },
                onError = {
                    getLocalCurrencyAndCreateAccount(note)
                },
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
            postEffect(Effect.NavigateBack)
        }
    }
}