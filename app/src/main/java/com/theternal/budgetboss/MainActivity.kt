package com.theternal.budgetboss

import android.os.Bundle
import android.view.View
import androidx.core.view.forEach
import androidx.navigation.FloatingWindow
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.fragment.NavHostFragment
import com.theternal.budgetboss.databinding.ActivityMainBinding
import com.theternal.common.extensions.gone
import com.theternal.common.extensions.hide
import com.theternal.common.extensions.safeNavigate
import com.theternal.common.extensions.show
import com.theternal.core.base.ActivityInflater
import com.theternal.core.base.BaseActivity
import com.theternal.core.base.Initializer
import com.theternal.core.managers.LayoutManager
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), LayoutManager {

    private var navController: NavController? = null

    override val inflateBinding: ActivityInflater<ActivityMainBinding>
        get() = ActivityMainBinding::inflate

    override val initViews: Initializer<ActivityMainBinding> = {

        navController = (supportFragmentManager.findFragmentById(
            R.id.activityContainerView
        ) as NavHostFragment).navController


        bottomNavBar.setOnItemSelectedListener {
            val destinationId = it.itemId
            val isAddNavGraph = destinationId == R.id.create_record_nav_graph
            if(!isAddNavGraph) {
                navController!!.popBackStack(
                    destinationId,
                    false
                )
            }
            navController.safeNavigate(destinationId)
            !isAddNavGraph
        }

        val weakReference = WeakReference(bottomNavBar)
        navController?.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    val view = weakReference.get()
                    if (view == null) {
                        navController?.removeOnDestinationChangedListener(this)
                        return
                    }
                    if (destination is FloatingWindow) {
                        return
                    }
                    view.menu.forEach { item ->
                        if (destination.hierarchy.any { it.id == item.itemId }) {
                            item.isChecked = true
                        }
                    }
                }
            })


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

    override fun showBackBtn() = binding.goBackBtn.show()
    override fun hideBackBtn() = binding.goBackBtn.gone()
    override fun showSettingsBtn() = binding.settingsBtn.show()
    override fun hideSettingsBtn() = binding.settingsBtn.hide()
    override fun showNavBar() = binding.main.transitionToEnd()
    override fun hideNavBar() = binding.main.transitionToStart()

    override fun setTitle(title: String) { binding.pageTitle.text = title }
}