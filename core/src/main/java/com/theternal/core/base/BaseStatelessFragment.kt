package com.theternal.core.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.Transition
import androidx.viewbinding.ViewBinding
import com.google.android.material.transition.MaterialContainerTransform

typealias Inflater<T> = (LayoutInflater, ViewGroup?, Boolean) -> T
typealias Initializer <T> = T.() -> Unit

abstract class BaseStatelessFragment<VB : ViewBinding> : Fragment() {

    //! Properties
    abstract val inflateBinding: Inflater<VB>

    private var _binding: VB? = null

    val binding: VB
        get() = _binding!!


    //! Lifecycle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentTransitions()
        initViews()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    //! Initializers
    open val initViews: Initializer<VB> = {}

    private fun initViews() {
        if(_binding != null) initViews(_binding!!)
    }

    //! Transitions
    open val viewEntering: Transition? = null
    open val viewExiting: Transition? = null

    open val sharedElementTransition: Transition = MaterialContainerTransform().apply {
        scrimColor = Color.TRANSPARENT
    }

    open val transitionDuration: Long = 500

    private fun setFragmentTransitions() {
        //? Screen Transitions
        enterTransition = viewEntering?.apply { duration = transitionDuration }
        exitTransition = viewExiting?.apply { duration = transitionDuration }
        //? Shared Element Transition (with another name, Hero)
        sharedElementEnterTransition = sharedElementTransition.apply {
            duration = transitionDuration
        }
    }

    //! Additional Functions
    fun binding(block: VB.() -> Unit) {
        _binding?.apply(block)
    }
}