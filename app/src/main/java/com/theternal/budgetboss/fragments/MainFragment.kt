package com.theternal.budgetboss.fragments

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Transition
import com.google.android.material.transition.MaterialElevationScale
import com.theternal.budgetboss.R
import com.theternal.budgetboss.databinding.FragmentMainBinding
import com.theternal.core.base.BaseStatelessFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.core.managers.ActivityNavManager

class MainFragment : BaseStatelessFragment<FragmentMainBinding>() {

    private var navController: NavController? = null

    override val inflateBinding: Inflater<FragmentMainBinding>
        get() = FragmentMainBinding::inflate

    override val initViews: Initializer<FragmentMainBinding> = {
        navController = (childFragmentManager.findFragmentById(
            R.id.mainContainerView
        ) as NavHostFragment).navController

        bottomNavBar.setupWithNavController(navController!!)

        addBtn.setOnClickListener {
            (requireActivity() as ActivityNavManager).navigateToAdd()
        }
    }

    override val viewEntering: Transition = MaterialElevationScale(true)
    override val viewExiting: Transition? = null

    override fun onDestroyView() {
        navController = null
        super.onDestroyView()
    }
}