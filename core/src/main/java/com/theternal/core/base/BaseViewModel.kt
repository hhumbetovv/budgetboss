package com.theternal.core.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theternal.core.base.interfaces.*
import com.theternal.core.domain.NetworkRequest
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


abstract class BaseViewModel<Event: ViewEvent, State: ViewState, Effect: ViewEffect> : ViewModel() {

    //! Getters
    private val initialState: State by lazy {
        createState()
    }

    abstract fun createState(): State

    val currentState: State
        get() = uiState.value

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<Event> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _uiEffect: Channel<Effect> = Channel()
    val uiEffect = _uiEffect.receiveAsFlow()

    //! Initializers
    init {
        _uiEvent.onEach { event ->
            onEventUpdate(event)
        }.launchIn(viewModelScope)
    }

    //! Setters
    protected fun setState(state: State) {
        viewModelScope.launch {
            _uiState.emit(state)
        }
    }

    fun postEvent(event: Event) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    fun postEffect(effect: Effect) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }

    protected open fun onEventUpdate(event: Event) {}

    //! Networking
    suspend fun <R> makeRequest(
        request: NetworkRequest<R>,
        onError: ((Exception) -> Unit)? = null,
        onSuccess: ((R) -> Unit)? = null
    ) {
        val result = request.invoke()
        if(result.isSuccess) {
            onSuccess?.invoke(result.getData)
        } else {
            onError?.invoke(result.getException)
            globalErrorHandler(result.getException)
        }
    }

    private fun globalErrorHandler(exception: Exception) {
        Log.e("ERROR", exception.message.toString())
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            _uiEvent.collect()
            _uiState.collect()
            _uiEffect.cancel()
        }
        viewModelScope.cancel()
    }
}
