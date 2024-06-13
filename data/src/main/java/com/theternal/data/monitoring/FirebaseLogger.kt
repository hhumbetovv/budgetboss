package com.theternal.data.monitoring

import com.google.firebase.analytics.ktx.analytics
import com.theternal.domain.interfaces.Logger
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase

class FirebaseLogger : Logger {
    override fun log(event: String, vararg params: Pair<String, String>) {
        Firebase.analytics.logEvent(event) {
            params.forEach {
                param(it.first, it.second)
            }
        }
    }
}