package com.theternal.common.extensions

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController?.safeNavigate(target: Int) {
    this?.apply {
        Log.d("SafeNavigate", target.toString())
        val current = currentDestination?.id
        Log.d("SafeNavigate", current.toString())
        if(current != target && graph.id != target) {
            navigate(target)
        }
    }
}

fun NavController?.safeNavigate(direction: NavDirections) {
    this?.apply {
        val current = currentDestination?.id
        val target = graph.getAction(direction.actionId)?.destinationId
        if(current != target) {
            navigate(direction)
        }
    }
}