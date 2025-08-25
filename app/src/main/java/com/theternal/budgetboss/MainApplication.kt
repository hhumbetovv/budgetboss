package com.theternal.budgetboss

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Firebase.analytics.setAnalyticsCollectionEnabled(true)
    }
}