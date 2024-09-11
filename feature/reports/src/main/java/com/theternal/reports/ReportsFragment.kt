package com.theternal.reports

import android.view.Gravity
import androidx.transition.Transition
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.SlideDistanceProvider
import com.theternal.core.base.BaseStatelessFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.core.managers.ToolbarManager
import com.theternal.reports.databinding.FragmentReportsBinding
import com.theternal.common.R.string as Strings

class ReportsFragment : BaseStatelessFragment<FragmentReportsBinding>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentReportsBinding>
        get() = FragmentReportsBinding::inflate

    //! Screen Transitions
    private val viewTransition = MaterialFadeThrough().apply {
        secondaryAnimatorProvider = SlideDistanceProvider(Gravity.END)
    }
    override val transitionDuration: Long = 300
    override val viewEntering: Transition = viewTransition
    override val viewExiting: Transition = viewTransition

    //! UI Properties
    private var pagerAdapter: ReportsPagerAdapter? = null
    private val tabTitles = listOf(
        Strings.categories,
        Strings.accounts
    )

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentReportsBinding> = {
        (requireActivity() as ToolbarManager).apply {
            showSettingsIcon()
            hideBackIcon()
            setTitle(getString(Strings.reports))
        }

        pagerAdapter = ReportsPagerAdapter(childFragmentManager, lifecycle)

        pager.adapter = pagerAdapter

        TabLayoutMediator(tabBar, pager) { tab, position ->
            tab.text = getString(tabTitles[position])
        }.attach()
    }

}