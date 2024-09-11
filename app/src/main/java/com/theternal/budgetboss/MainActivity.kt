package com.theternal.budgetboss

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.theternal.budgetboss.databinding.ActivityMainBinding
import com.theternal.common.extensions.fadeOut
import com.theternal.common.extensions.hide
import com.theternal.common.extensions.safeNavigate
import com.theternal.common.extensions.show
import com.theternal.core.base.ActivityInflater
import com.theternal.core.base.BaseActivity
import com.theternal.core.base.Initializer
import com.theternal.core.managers.ActivityNavManager
import com.theternal.core.managers.ToolbarManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), ActivityNavManager, ToolbarManager {

    private var navController: NavController? = null

    override val inflateBinding: ActivityInflater<ActivityMainBinding>
        get() = ActivityMainBinding::inflate

    override val initViews: Initializer<ActivityMainBinding> = {

        navController = (supportFragmentManager.findFragmentById(
            R.id.activityContainerView
        ) as NavHostFragment).navController

        goBackBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        settingsBtn.setOnClickListener {
            navController.safeNavigate(com.theternal.settings.R.id.settings_nav_graph)
        }
    }

    override fun onDestroy() {
        navController = null
        super.onDestroy()
    }

    override fun navigateToAdd() {
        navController?.safeNavigate(com.theternal.add_record.R.id.add_nav_graph)
    }

    override fun navigateToMain() {
        navController?.safeNavigate(R.id.mainFragment)
    }

    override fun showBackIcon() {
        binding.apply {
            goBackBtn.show()
        }
    }

    override fun hideBackIcon() {
        binding.apply {
            goBackBtn.hide()
        }
    }

    override fun showSettingsIcon() {
        binding.settingsBtn.show()
    }

    override fun hideSettingsIcon() {
        binding.settingsBtn.fadeOut()
    }

    override fun setTitle(title: String) {
        binding.pageTitle.text = title
    }
}