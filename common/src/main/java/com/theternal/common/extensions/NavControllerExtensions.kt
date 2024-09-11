package com.theternal.common.extensions

import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController?.safeNavigate(target: Int) {
    this?.apply {
        val current = currentDestination?.id
        if(current != target) {
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