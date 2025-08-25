package com.theternal.data.monitoring

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.theternal.domain.interfaces.Logger
import com.google.firebase.analytics.logEvent

class FirebaseLogger : Logger {
    override fun log(event: String, vararg params: Pair<String, String>) {
        Firebase.analytics.logEvent(event) {
            params.forEach {
                param(it.first, it.second)
            }
        }
    }
}