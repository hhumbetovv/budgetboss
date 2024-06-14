package com.theternal.budgetboss

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.theternal.budgetboss.databinding.ActivityMainBinding
import com.theternal.common.extensions.fadeOut
import com.theternal.common.extensions.show
import com.theternal.core.base.ActivityInflater
import com.theternal.core.base.BaseActivity
import com.theternal.core.base.Initializer
import com.theternal.core.managers.NavigationManager
import com.theternal.core.managers.ToolbarManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), NavigationManager, ToolbarManager {

    private var navController: NavController? = null

    override val inflateBinding: ActivityInflater<ActivityMainBinding>
        get() = ActivityMainBinding::inflate

    override val initViews: Initializer<ActivityMainBinding> = {
        navController = (supportFragmentManager.findFragmentById(
            R.id.activityContainerView
        ) as NavHostFragment).navController

        binding.goBackBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
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

    override fun showBackIcon() {
        binding.apply {
            goBackBtn.show()
            pageTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
    }

    override fun hideBackIcon() {
        binding.apply {
            goBackBtn.fadeOut()
            pageTitle.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        }
    }

    override fun setTitle(title: String) {
        binding.pageTitle.text = title
    }
}