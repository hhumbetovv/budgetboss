package com.theternal.core.managers

interface LayoutManager {
    fun showBackBtn()
    fun showSettingsBtn()
    fun showNavBar()
    fun hideBackBtn()
    fun hideSettingsBtn()
    fun hideNavBar()

    fun setTitle(title: String)
}