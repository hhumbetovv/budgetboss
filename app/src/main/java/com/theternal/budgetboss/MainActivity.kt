package com.theternal.budgetboss

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.theternal.budgetboss.databinding.ActivityMainBinding
import com.theternal.core.base.ActivityInflater
import com.theternal.core.base.BaseActivity
import com.theternal.core.base.Initializer
import com.theternal.core.managers.NavigationManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), NavigationManager {

    private var navController: NavController? = null

    override val inflateBinding: ActivityInflater<ActivityMainBinding>
        get() = ActivityMainBinding::inflate

    override val initViews: Initializer<ActivityMainBinding> = {
        navController = (supportFragmentManager.findFragmentById(
            R.id.activityContainerView
        ) as NavHostFragment).navController
    }

    override fun onDestroy() {
        navController = null
        super.onDestroy()
    }

    override fun navigateToAdd() {
        navController?.navigate(com.theternal.add_record.R.id.add_nav_graph)
    }

    override fun navigateToMain() {
        navController?.navigate(R.id.mainFragment)
    }
}