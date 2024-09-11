package com.theternal.core.managers

interface ToolbarManager {
    fun showBackIcon()
    fun hideBackIcon()
    fun showSettingsIcon()
    fun hideSettingsIcon()

    fun setTitle(title: String)
}