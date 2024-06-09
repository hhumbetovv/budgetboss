package com.theternal.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.theternal.core.base.interfaces.*
import kotlinx.coroutines.Job

abstract class BaseStatefulFragment<VB : ViewBinding, VM : BaseViewModel<Event, State, Effect>,
        Event: ViewEvent, State: ViewState, Effect: ViewEffect> : BaseStatelessFragment<VB>() {

    //! Properties
    abstract val getViewModelClass: () -> Class<VM>
    open val hasSharedViewModel = false

    private var _viewModel: VM? = null

    val state
        get() = _viewModel?.currentState

    private var stateJob: Job? = null
    private var effectJob: Job? = null

    //! Lifecycle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewModel = ViewModelProvider(
            if(hasSharedViewModel) requireActivity()
            else this
        )[getViewModelClass()]
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        stateJob?.cancel()
        effectJob?.cancel()
        _viewModel = null
        super.onDestroyView()
    }

    //! Initializers
    private fun initViewModel() {
        //! State Listener
       stateJob = _viewModel!!.uiState.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { state -> onStateUpdate(state) }.launchIn(lifecycleScope)
        //! Effect Listener
        effectJob = _viewModel!!.uiEffect.flowWithLifecycle(
            lifecycle, Lifecycle.State.STARTED
        ).onEach { effect -> onEffectUpdate(effect) }.launchIn(lifecycleScope)
    }

    protected open fun onStateUpdate(state: State) {}
    protected open fun onEffectUpdate(effect: Effect) {}

    //! Setters
    protected open fun postEvent(event: Event) {
        if(_viewModel != null) {
            _viewModel!!.postEvent(event)
        }
    }
}