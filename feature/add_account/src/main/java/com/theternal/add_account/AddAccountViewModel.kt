package com.theternal.add_account

import androidx.lifecycle.viewModelScope
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
                    setState(currentState.copy(currencyList = list, isLoading = false))
                },
                onError = {
                    viewModelScope.launch(Dispatchers.IO) {
                        setState(
                            currentState.copy(
                                currencyList = getLocalCurrencyListUseCase(),
                                isLoading = false
                            )
                        )
                    }
                },
            )
        }
    }

    override fun onEventUpdate(event: Event) {
        when(event) {
            is Event.SetAccountName -> {
                setState(currentState.copy(accountName = event.accountName))
            }
            is Event.SetBalance -> {
                setState(currentState.copy(balance = event.balance))
            }
            is Event.SetCurrency -> {
                setState(currentState.copy(currency = event.currency))
            }
            is Event.CreateAccount -> {
                createAccount(event.note)
            }
        }
    }

    private fun createAccount(note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            setState(currentState.copy(isLoading = true))
            makeRequest(
                NetworkRequest.WithParams(
                    exchangeUseCase::invoke,
                    ExchangeUseCase.Params(currentState.currency!!)
                )
            ) { currencyValue ->
                viewModelScope.launch(Dispatchers.IO) {
                    createAccountUseCase(
                        CreateAccountUseCase.Params(
                            AccountEntity(
                                name = currentState.accountName,
                                balance = currentState.balance ?: BigDecimal.ZERO,
                                currency = currentState.currency!!,
                                note = note
                            ),
                            currencyValue
                        )
                    )
                    postEffect(Effect.NavigateBack)
                }
            }
            setState(currentState.copy(isLoading = false))
        }
    }
}